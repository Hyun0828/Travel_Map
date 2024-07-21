package travel.travel.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


/**
 * 네이버 인증서버가 제공하는 accessToken을 받아오기 위한 API를 통해 받아오는 DTO
 */

@JsonNaming(value = SnakeCaseStrategy.class)
public record NaverToken(
        String accessToken,
        String refreshToken,
        String tokenType,
        Integer expiresIn,
        String error,
        String errorDescription
) {
}