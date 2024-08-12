package travel.travel.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import travel.travel.domain.CommonUser;
import travel.travel.domain.User;
import travel.travel.domain.oauth.OauthUser;
import travel.travel.exception.TokenInvalidException;
import travel.travel.jwt.service.JwtService;
import travel.travel.jwt.util.PasswordUtil;
import travel.travel.repository.UserRepository;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 * <p>
 * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
 * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
 * <p>
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는 않는다.
 * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 ERROR
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급(RTR 방식)
 * 인증 성공 처리는 하지 않고 실패 처리
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login"; // "/login"으로 들어오는 요청은 Filter 작동 X

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if (request.getRequestURI().equals(NO_CHECK_URL)) {
//            log.debug("Request URI is /login, skipping filter.");
//            filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
//            return; // return으로 이후 현재 필터 진행 막기 (안 해주면 아래로 내려가서 계속 필터 진행시킴)
//        }
        String path = request.getRequestURI();
        if (path.startsWith("/login") || path.startsWith("/oauth") || path.startsWith("/sign-up") || path.startsWith("/reissue") || path.startsWith("/google-login")) {
            log.debug("JWT Authentication Filter Skip");
            filterChain.doFilter(request, response);
            return;
        }

        // AccessToken 체크 및 인증 처리
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    /**
     * [AccessToken 체크 & 인증 처리 메소드]
     * request에서 extractAccessToken()으로 AccessToken 추출 후, isTokenValid()로 유효한 토큰인지 검증
     * 유효한 토큰이면, AccessToken서 extractEmail로 Email을 추출한 후 findByEmail()로 해당 이메일을 사용하는 유저 객체 반환
     * 그 유저 객체를 saveAuthentication()으로 인증 처리하여
     * 인증 허가 처리된 객체를 SecurityContextHolder에 담기
     * 그 후 다음 인증 필터로 진행
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");

//        jwtService.extractAccessToken(request)
//                .filter(jwtService::isTokenValid)
//                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)
//                        .ifPresent(email -> userRepository.findByEmail(email)
//                                .ifPresent(this::saveAuthentication)));
        String accessToken = jwtService.extractAccessToken(request).orElse(null);

        if (accessToken == null) {
            log.info("2");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다");
            return;
        }

        try {
            log.info("3");
            jwtService.isTokenValid(accessToken);
            jwtService.extractEmail(accessToken)
                    .ifPresent(email -> userRepository.findByEmail(email)
                            .ifPresent(this::saveAuthentication));
        } catch (TokenInvalidException e) {
            log.info("4");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        } catch (RuntimeException e) {
            log.info("5");
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "토큰 오류 : " + e.getMessage());
            return;
        }

        log.info("6");
        filterChain.doFilter(request, response);
    }

    /**
     * [인증 허가 메소드]
     * 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetails의 User 객체
     * <p>
     * new UsernamePasswordAuthenticationToken()로 인증 객체인 Authentication 객체 생성
     * UsernamePasswordAuthenticationToken의 파라미터
     * 1. 위에서 만든 UserDetailsUser 객체 (유저 정보)
     * 2. credential(보통 비밀번호로, 인증 시에는 보통 null로 제거)
     * 3. Collection < ? extends GrantedAuthority>로,
     * UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에,
     * new NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
     * <p>
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     */
    public void saveAuthentication(User user) {

        // OAuth2 로그인 유저는 password가 없다. 그래서 임의의 password를 만들어서 넣어준다.
        String password = null;
        if (user.getClass() == CommonUser.class)
            password = ((CommonUser) user).getPassword();
        else if (user.getClass() == OauthUser.class)
            password = PasswordUtil.generateRandomPassword();

        assert password != null;
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(password)
                .roles(user.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 오류 정보 {status, message}를 JSON 형태로 바꿔서 응답
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        ErrorResponse errorResponse = new ErrorResponse(statusCode, message);
        mapper.writeValue(out, errorResponse);
    }

    public static class ErrorResponse {
        private int status;
        private String message;

        // Constructors, getters, and setters
        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
