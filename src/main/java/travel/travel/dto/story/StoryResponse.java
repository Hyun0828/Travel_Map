package travel.travel.dto.story;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class StoryResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoryInfoResponseDTO {
        private Long id;
        private String title;
        private String content;
        private String place;
        private String address;
        private LocalDate date;
    }
}
