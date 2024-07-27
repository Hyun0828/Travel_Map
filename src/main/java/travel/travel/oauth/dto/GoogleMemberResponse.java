package travel.travel.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import travel.travel.domain.Role;
import travel.travel.domain.oauth.OauthId;
import travel.travel.domain.oauth.OauthUser;

import static travel.travel.oauth.OauthServerType.GOOGLE;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleMemberResponse(
        String id,
        String email,
        boolean verified_email,
        String name,
        String given_name,
        String family_name,
        String picture,
        String locale
) {

    public OauthUser toDomain() {
        return OauthUser.builder()
                .oauthId(new OauthId(id, GOOGLE))
                .email(email)
                .name(name)
                .profileImageUrl(picture)
                .role(Role.GUEST)
                .build();
    }
}
