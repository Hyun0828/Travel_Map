package travel.travel.mapper;

import travel.travel.domain.Story;
import travel.travel.dto.story.StoryRequest;
import travel.travel.dto.story.StoryResponse;

public class StoryMapper {

    public static Story toStoryFromStoryCreateRequestDto(StoryRequest.StoryCreateRequestDTO storyCreateRequestDto) {
        return Story.builder()
                .title(storyCreateRequestDto.getTitle())
                .content(storyCreateRequestDto.getContent())
                .address(storyCreateRequestDto.getAddress())
                .place(storyCreateRequestDto.getPlace())
                .date(storyCreateRequestDto.getDate().toLocalDate())
                .build();
    }

    public static StoryResponse.StoryInfoResponseDTO toStoryInfoResponseDto(Story story) {
        return StoryResponse.StoryInfoResponseDTO.builder()
                .id(story.getId())
                .title(story.getTitle())
                .content(story.getContent())
                .place(story.getPlace())
                .address(story.getAddress())
                .date(story.getDate())
                .build();
    }
}
