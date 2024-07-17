package travel.travel.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class UserImageRequestDto {

    private MultipartFile imageFile;

    @Builder
    public UserImageRequestDto(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}