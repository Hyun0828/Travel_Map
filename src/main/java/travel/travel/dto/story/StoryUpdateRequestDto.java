package travel.travel.dto.story;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StoryUpdateRequestDto {

    private String title;
    private String content;
    private String place;
    private String address;
    private LocalDateTime date;
}
