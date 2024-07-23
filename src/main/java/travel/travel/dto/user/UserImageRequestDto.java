package travel.travel.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class CommonUserImageRequestDto {

    private MultipartFile imageFile;

    @Builder
    public CommonUserImageRequestDto(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}