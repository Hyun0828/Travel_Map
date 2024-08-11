package travel.travel.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.travel.jwt.service.JwtService;
import travel.travel.repository.RefreshRepository;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReIssueController {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 Refresh token 꺼내기
        Optional<String> refreshToken = jwtService.extractRefreshToken(request);
        if (refreshToken.isEmpty())
            return new ResponseEntity<>("RefreshToken이 없습니다", HttpStatus.BAD_REQUEST);

        // refreshToken 유효성 검사
        try {
            jwtService.isTokenValid(refreshToken.get());
        } catch (Exception e) {
            return new ResponseEntity<>("RefreshToken이 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
//        if (!jwtService.isTokenValid(refreshToken.get()))
//            return new ResponseEntity<>("RefreshToken이 유효하지 않습니다.", HttpStatus.BAD_REQUEST);

        // 블랙리스트 여부 검사 (DB에 저장되어 있는지)
        if (!refreshRepository.existsByRefresh(refreshToken.get()))
            return new ResponseEntity<>("RefreshToken이 유효하지 않습니다.", HttpStatus.BAD_REQUEST);

        // 새로운 accessToken, refreshToken 발급
        String email = refreshRepository.findByRefresh(refreshToken.get()).get().getEmail();
        String new_accessToken = jwtService.createAccessToken(email); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
        String new_refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급

        jwtService.sendAccessAndRefreshToken(response, new_accessToken, new_refreshToken); // 응답 헤더에 AccessToken, 응답 쿠키에 RefreshToken 실어서 응답
        jwtService.deleteRefreshToken(refreshToken.get());      // DB에 기존 refreshToken 삭제
        jwtService.updateRefreshToken(email, new_refreshToken); // DB에 새로운 refreshToken 저장

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

