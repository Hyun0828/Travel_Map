package travel.travel.dto.story;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
public class StoryImageRequestDto {

    private List<MultipartFile> multipartFile;
}
