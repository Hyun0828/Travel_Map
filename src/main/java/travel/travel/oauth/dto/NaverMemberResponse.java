package travel.travel.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import travel.travel.domain.Role;
import travel.travel.domain.oauth.OauthId;
import travel.travel.domain.oauth.OauthUser;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

import static travel.travel.oauth.OauthServerType.NAVER;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NaverMemberResponse(
        String resultcode,
        String message,
        Response response
) {

    public OauthUser toDomain() {
        return OauthUser.builder()
                .oauthId(new OauthId(String.valueOf(response.id), NAVER))
                .name(response.name)
                .profileImageUrl(response.profileImage)
                .email(response.email)
                .role(Role.GUEST)
                .age(calculateAge(response.birthyear))
                .gender(response.gender)
                .birth(getBirthDate(response().birthyear, response.birthday))
                .build();
    }

    public LocalDate getBirthDate(String birthyear, String birthday) {
        if (birthyear != null && birthday != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(birthyear + "-" + birthday, formatter);
        }
        return null;
    }

    public Integer calculateAge(String birthyear) {
        if (birthyear != null && !birthyear.isEmpty()) {
            try {
                int birthYear = Integer.parseInt(birthyear);
                int currentYear = Year.now().getValue();
                return currentYear - birthYear + 1;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Response(
            String id,
            String nickname,
            String name,
            String email,
            String gender,
            String age,
            String birthday,
            String profileImage,
            String birthyear,
            String mobile
    ) {
    }
}