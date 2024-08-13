package travel.travel.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import travel.travel.domain.RefreshToken;
import travel.travel.exception.TokenInvalidException;
import travel.travel.repository.CommonUserRepository;
import travel.travel.repository.RefreshRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    /**
     * JWT의 Subject와 Claim으로 email 사용 -> 클레임의 name을 "email"으로 설정
     * JWT의 헤더에 들어오는 값 : 'Authorization(Key) = Bearer {토큰} (Value)' 형식
     */
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";
    private final CommonUserRepository commonUserRepository;
    private final RefreshRepository refreshRepository;
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.access.expiration}")
    private Integer accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Integer refreshTokenExpirationPeriod;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    /**
     * AccessToken 생성 메소드
     */
    public String createAccessToken(String email) {
        Date now = new Date();
        return JWT.create() // JWT 토큰을 생성하는 빌더 반환
                .withSubject(ACCESS_TOKEN_SUBJECT) // JWT의 Subject 지정 -> AccessToken이므로 AccessToken
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료 시간 설정

                //클레임으로는 저희는 email 하나만 사용합니다.
                //추가적으로 식별자나, 이름 등의 정보를 더 추가하셔도 됩니다.
                //추가하실 경우 .withClaim(클래임 이름, 클래임 값) 으로 설정해주시면 됩니다
                .withClaim("unique_id", UUID.randomUUID().toString())
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secretKey)); // HMAC512 알고리즘 사용, application-jwt.yml에서 지정한 secret 키로 암호화
    }

    /**
     * RefreshToken 생성
     * RefreshToken은 Claim에 email도 넣지 않으므로 withClaim() X
     */
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .withClaim("unique_id", UUID.randomUUID().toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * AccessToken 헤더에 실어서 보내기 : AccessToken 재발급
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    /**
     * AccessToken 헤더 + RefreshToken 쿠키에 실어서 보내기 : 로그인 시 AccessToken, RefreshToken 동시 발급
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenCookie(response, refreshToken);
        log.info("Access Token 헤더, Refresh Token 쿠키 설정 완료");
    }

    /**
     * 쿠키에서 RefreshToken 추출
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(refreshHeader))
                refresh = cookie.getValue();
        }

        return Optional.ofNullable(refresh);
    }

    /**
     * 헤더에서 AccessToken 추출
     * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
     * 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * AccessToken에서 Email 추출
     * 추출 전에 JWT.require()로 검증기 생성
     * verify로 AceessToken 검증 후
     * 유효하다면 getClaim()으로 이메일 추출
     * 유효하지 않다면 빈 Optional 객체 반환
     */
    public Optional<String> extractEmail(String accessToken) {
        try {
            // 토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier builder 반환
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build() // 반환된 빌더로 JWT verifier 생성
                    .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생
                    .getClaim(EMAIL_CLAIM) // claim(Emial) 가져오기
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    /**
     * AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    /**
     * RefreshToken 쿠키 설정
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        response.addCookie(createCookie(refreshHeader, refreshToken));
    }

    /**
     * 기존 RefreshToken DB에서 삭제
     */
    public void deleteRefreshToken(String refreshToken) {
        if (refreshRepository.existsByRefresh(refreshToken))
            refreshRepository.deleteByRefresh(refreshToken);
    }

    /**
     * RefreshToken DB 저장(업데이트) - Redis가 아닌 새로운 Table에 저장
     */
    public void updateRefreshToken(String email, String refreshToken) {
        refreshRepository.save(RefreshToken.builder()
                .email(email)
                .refresh(refreshToken)
                .expiration(refreshTokenExpirationPeriod)
                .build());
    }

    /**
     * 토큰 유효성 검사
     */
    public void isTokenValid(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new TokenInvalidException("유효하지 않은 토큰");
        } catch (Exception e) {
            throw new RuntimeException("토큰 검증 중 오류 발생" , e);
        }
    }

//    public boolean isTokenValid(String token) {
//        try {
//            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
//            return true;
//        } catch (Exception e) {
//            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
//            return false;
//        }
//    }

    /**
     * 쿠키 생성
     */
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(refreshTokenExpirationPeriod);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        return cookie;
    }
}