package travel.travel.mapper;

import travel.travel.domain.Story;
import travel.travel.dto.story.StoryCreateRequestDto;

public class StoryMapper {

    public static Story toStoryFromStoryCreateRequestDto(StoryCreateRequestDto storyCreateRequestDto) {
        return Story.builder()
                .title(storyCreateRequestDto.getTitle())
                .content(storyCreateRequestDto.getContent())
                .address(storyCreateRequestDto.getAddress())
                .place(storyCreateRequestDto.getPlace())
                .date(storyCreateRequestDto.getDate().toLocalDate())
                .mapx(Integer.parseInt(storyCreateRequestDto.getMapx()))
                .mapy(Integer.parseInt(storyCreateRequestDto.getMapy()))
                .build();
    }
}
