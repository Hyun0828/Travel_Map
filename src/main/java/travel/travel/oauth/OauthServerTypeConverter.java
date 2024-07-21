package travel.travel.oauth;

import org.springframework.core.convert.converter.Converter;

/**
 * String을 OauthServerType으로 변환해주는 Converter
 */

public class OauthServerTypeConverter implements Converter<String, OauthServerType> {

    @Override
    public OauthServerType convert(String source) {
        return OauthServerType.fromName(source);
    }
}
