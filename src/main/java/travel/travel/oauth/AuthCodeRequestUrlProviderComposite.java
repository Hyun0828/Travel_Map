package travel.travel.oauth;

import org.springframework.stereotype.Component;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * OAuthServerType의 종류에 따라 해당하는 AuthCodeRequestUrlProvider를 사용하여 URL을 생성한다.
 */

@Component
public class AuthCodeRequestUrlProviderComposite {

    private final Map<OauthServerType, AuthCodeRequestUrlProvider> mapping;

    public AuthCodeRequestUrlProviderComposite(Set<AuthCodeRequestUrlProvider> providers) {
        mapping = providers.stream()
                .collect(toMap(
                        AuthCodeRequestUrlProvider::supportServer,
                        identity()
                ));
    }

    public String provide(OauthServerType oauthServerType) {
        return getProvider(oauthServerType).provide();
    }

    private AuthCodeRequestUrlProvider getProvider(OauthServerType oauthServerType) {
        return Optional.ofNullable(mapping.get(oauthServerType))
                .orElseThrow(() -> new RuntimeException("지원하지 않는 소셜 로그인 타입입니다."));
    }
}