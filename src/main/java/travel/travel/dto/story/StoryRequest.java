package travel.travel.dto.story;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class StoryRequest {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoryCreateRequestDTO {
        private String title;
        private String content;
        private String place;
        private String address;
        private LocalDateTime date;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoryImageRequestDTO {
        private List<MultipartFile> multipartFile;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoryUpdateRequestDTO {
        private String title;
        private String content;
        private String place;
        private String address;
        private LocalDateTime date;
    }
}
