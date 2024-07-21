package travel.travel.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleToken(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        String scope,
        String tokenType
) {
}
