package travel.travel.oauth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.domain.oauth.OauthUser;
import travel.travel.oauth.AuthCodeRequestUrlProviderComposite;
import travel.travel.oauth.OauthServerType;
import travel.travel.oauth.client.OauthMemberClientComposite;
import travel.travel.repository.OauthMemberRepository;

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
    private final OauthMemberRepository oauthMemberRepository;

    public String getAuthCodeRequestUrl(OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    //TODO JWT 토큰 생성 후 반환
    public Long login(OauthServerType oauthServerType, String authCode) {
        OauthUser oauthUser = oauthMemberClientComposite.fetch(oauthServerType, authCode);
        OauthUser saved = oauthMemberRepository.findByOauthId(oauthUser.getOauthId())
                .orElseGet(() -> oauthMemberRepository.save(oauthUser));
        return saved.getId();
    }
}

