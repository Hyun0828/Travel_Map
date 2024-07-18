package travel.travel.logout;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;
import travel.travel.jwt.service.JwtService;
import travel.travel.repository.RefreshRepository;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain) throws IOException, ServletException {

        // 로그아웃 API call 인지 확인
        String requestURI = request.getRequestURI();
        if (!requestURI.matches("^\\/logout$")) {
            filterchain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterchain.doFilter(request, response);
            return;
        }

        // refreshToken 꺼내기
        Optional<String> refreshToken = jwtService.extractRefreshToken(request);

        // token이 없으면
        if (refreshToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // token이 유효하지 않으면
        if (!jwtService.isTokenValid(refreshToken.get())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // token이 블랙리스트인지 확인
        if (!refreshRepository.existsByRefresh(refreshToken.get())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 로그아웃 진행 : DB에서 refreshToken 삭제
        refreshRepository.deleteByRefresh(refreshToken.get());

        // Cookie에서 refreshToken 값을 0으로 변경
        Cookie cookie = new Cookie(jwtService.getRefreshHeader(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}