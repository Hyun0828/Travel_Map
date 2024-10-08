package travel.travel.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.travel.apiPayload.ApiResponse;
import travel.travel.apiPayload.code.status.ErrorStatus;
import travel.travel.jwt.service.JwtService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReIssueController {

    private final JwtService jwtService;
//    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/reissue")
    public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 Refresh token 꺼내기
        Optional<String> refreshToken = jwtService.extractRefreshToken(request);
        if (refreshToken.isEmpty())
            return ApiResponse.onFailure(ErrorStatus._REFRESHTOKEN_NOT_FOUND.getCode(), ErrorStatus._REFRESHTOKEN_NOT_FOUND.getMessage(), null);

        // refreshToken 유효성 검사
        try {
            jwtService.isTokenValid(refreshToken.get());
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus._REFRESHTOKEN_NOT_VALID.getCode(), ErrorStatus._REFRESHTOKEN_NOT_VALID.getMessage(), null);
        }

        // 블랙리스트 여부 검사 (DB에 저장되어 있는지)
//        if (!refreshTokenRepository.existsByRefresh(refreshToken.get()))
//            return ApiResponse.onFailure(ErrorStatus._REFRESHTOKEN_BLACKLIST.getCode(), ErrorStatus._REFRESHTOKEN_BLACKLIST.getMessage(), null);

        if (jwtService.isBlackList(refreshToken.get()))
            return ApiResponse.onFailure(ErrorStatus._REFRESHTOKEN_BLACKLIST.getCode(), ErrorStatus._REFRESHTOKEN_BLACKLIST.getMessage(), null);

        // 새로운 accessToken, refreshToken 발급
//        String email = refreshTokenRepository.findByRefresh(refreshToken.get()).get().getEmail();
        String email = jwtService.getEmail(refreshToken.get());
        String new_accessToken = jwtService.createAccessToken(email); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
        String new_refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급

        jwtService.sendAccessAndRefreshToken(response, new_accessToken, new_refreshToken); // 응답 헤더에 AccessToken, 응답 쿠키에 RefreshToken 실어서 응답
        jwtService.deleteRefreshToken(refreshToken.get());      // DB에 기존 refreshToken 삭제
        jwtService.updateRefreshToken(email, new_refreshToken); // DB에 새로운 refreshToken 저장

        return ApiResponse.onSuccess(null);
    }
}

