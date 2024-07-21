package travel.travel.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class UserSignUpRequestDto {

    private String email;
    private String password;
    private String name;
    private LocalDate birth;
    private Character gender;
    private Integer age;
    private String location;
}
