package travel.travel.oauth.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.domain.UserImage;
import travel.travel.domain.oauth.OauthUser;
import travel.travel.jwt.service.JwtService;
import travel.travel.oauth.AuthCodeRequestUrlProviderComposite;
import travel.travel.oauth.OauthServerType;
import travel.travel.oauth.client.OauthMemberClientComposite;
import travel.travel.repository.OauthUserRepository;
import travel.travel.repository.UserImageRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor

/**
 * OauthServerType을 받아서 해당 인증 서버에서 Auth Code를 받아오기 위한 URL 주소
 * 로그인
 */

public class OauthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final OauthUserRepository oauthUserRepository;
    private final UserImageRepository userImageRepository;
    private final JwtService jwtService;

    public String getAuthCodeRequestUrl(OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    public OauthUser login(HttpServletResponse response, OauthServerType oauthServerType, String authCode) {
        OauthUser oauthUser = oauthMemberClientComposite.fetch(oauthServerType, authCode);
        OauthUser saved = oauthUserRepository.findByOauthId(oauthUser.getOauthId())
                .orElseGet(() -> oauthUserRepository.save(oauthUser));

        String accessToken = jwtService.createAccessToken(oauthUser.getEmail()); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
        String refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 헤더에 AccessToken, 응답 쿠키에 RefreshToken 실어서 응답
        jwtService.updateRefreshToken(oauthUser.getEmail(), refreshToken); // DB에 RefreshToken 저장

        return saved;
    }

    public void saveImage(OauthUser user) {
        Optional<UserImage> image = userImageRepository.findByUser(user);
        if (image.isEmpty()) {
            UserImage userImage = UserImage.builder()
                    .user(user)
                    .url(user.getProfileImageUrl())
                    .build();

            userImage.setUser(user);
            userImageRepository.save(userImage);
        }
    }
}