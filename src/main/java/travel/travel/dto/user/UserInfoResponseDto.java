package travel.travel.dto.user;

import lombok.Builder;
import lombok.Getter;
import travel.travel.domain.Role;

import java.time.LocalDate;

@Getter
@Builder
public class UserInfoResponseDto {
    private String email;
    private String name;
    private LocalDate birth;
    private String gender;
    private Integer age;
    private String location;
    private String dtype;
    private Role role;
    private String imageUrl;
}
