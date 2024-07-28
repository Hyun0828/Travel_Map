package travel.travel.mapper;

import travel.travel.domain.Story;
import travel.travel.dto.story.StoryCreateRequestDto;
import travel.travel.dto.story.StoryInfoResponseDto;

public class StoryMapper {

    public static Story toStoryFromStoryCreateRequestDto(StoryCreateRequestDto storyCreateRequestDto) {
        return Story.builder()
                .title(storyCreateRequestDto.getTitle())
                .content(storyCreateRequestDto.getContent())
                .address(storyCreateRequestDto.getAddress())
                .place(storyCreateRequestDto.getPlace())
                .date(storyCreateRequestDto.getDate().toLocalDate())
                .build();
    }

    public static StoryInfoResponseDto toStoryInfoResponseDto(Story story) {
        return StoryInfoResponseDto.builder()
                .title(story.getTitle())
                .content(story.getContent())
                .place(story.getContent())
                .address(story.getAddress())
                .date(story.getDate())
                .build();
    }
}
