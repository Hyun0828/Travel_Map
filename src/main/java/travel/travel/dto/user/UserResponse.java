package travel.travel.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import travel.travel.domain.Role;

import java.time.LocalDate;

public class UserResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoResponseDTO {
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
}
