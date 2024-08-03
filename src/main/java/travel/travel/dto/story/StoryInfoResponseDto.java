package travel.travel.dto.story;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class StoryInfoResponseDto {

    private Long id;
    private String title;
    private String content;
    private String place;
    private String address;
    private LocalDate date;
}
