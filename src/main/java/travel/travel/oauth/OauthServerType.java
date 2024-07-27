package travel.travel.oauth;

import java.util.Locale;

public enum OauthServerType {

    KAKAO,
    NAVER,
    GOOGLE;

    public static OauthServerType fromName(String type) {
        return OauthServerType.valueOf(type.toUpperCase(Locale.ENGLISH));
    }
}
