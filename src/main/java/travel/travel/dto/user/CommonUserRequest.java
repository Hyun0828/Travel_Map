package travel.travel.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class CommonUserRequest {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommonUserSignUpRequestDTO{
        private String email;
        private String password;
        private String name;
        private LocalDate birth;
        private String gender;
        private Integer age;
        private String location;
    }
}
