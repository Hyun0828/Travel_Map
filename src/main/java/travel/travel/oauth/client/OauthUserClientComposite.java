package travel.travel.oauth.client;

import org.springframework.stereotype.Component;
import travel.travel.domain.oauth.OauthUser;
import travel.travel.oauth.OauthServerType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * OAuthServerType의 종류에 따라 해당하는 OauthUserClient를 사용하여 회원 객체를 생성한다.
 */

@Component
public class OauthUserClientComposite {

    private final Map<OauthServerType, OauthUserClient> mapping;

    public OauthUserClientComposite(Set<OauthUserClient> clients) {
        mapping = clients.stream()
                .collect(toMap(
                        OauthUserClient::supportServer,
                        identity()
                ));
    }

    public OauthUser fetch(OauthServerType oauthServerType, String authCode) {
        return getClient(oauthServerType).fetch(authCode);
    }

    private OauthUserClient getClient(OauthServerType oauthServerType) {
        return Optional.ofNullable(mapping.get(oauthServerType))
                .orElseThrow(() -> new RuntimeException("지원하지 않는 소셜 로그인 타입입니다."));
    }
}