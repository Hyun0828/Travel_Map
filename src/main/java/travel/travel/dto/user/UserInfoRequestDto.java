package travel.travel.dto.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserInfoRequestDto {
    private String email;
    private String name;
    private LocalDate birth;
    private String gender;
    private Integer age;
    private String location;
}
