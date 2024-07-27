package travel.travel.dto.story;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StoryCreateRequestDto {

    private String title;
    private String content;
    private String place;
    private String address;
    private LocalDateTime date;
    private String mapx;
    private String mapy;
}
