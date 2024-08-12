package travel.travel.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class UserRequest {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoRequestDTO {
        private String email;
        private String name;
        private LocalDate birth;
        private String gender;
        private Integer age;
        private String location;
    }
}
