package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.apiPayload.code.status.ErrorStatus;
import travel.travel.apiPayload.exception.handler.StoryHandler;
import travel.travel.domain.Story;
import travel.travel.domain.Writing;
import travel.travel.dto.story.StoryRequest;
import travel.travel.dto.story.StoryResponse;
import travel.travel.mapper.StoryMapper;
import travel.travel.repository.StoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final WritingService writingService;

    public Long create(StoryRequest.StoryCreateRequestDTO storyCreateRequestDto) {
        Story story = StoryMapper.toStoryFromStoryCreateRequestDto(storyCreateRequestDto);
        Story savedStory = storyRepository.save(story);
        return savedStory.getId();
    }

    public StoryResponse.StoryInfoResponseDTO upload(Long storyId) {
        Story story = storyRepository.findById(storyId).orElse(null);
        if (story == null)
            throw new StoryHandler(ErrorStatus._STORY_NOT_FOUND);
        return StoryMapper.toStoryInfoResponseDto(story);
    }

    public List<StoryResponse.StoryInfoResponseDTO> getByPaging(String accessToken, Integer page_number, Integer page_size) {
        List<Writing> writingList = writingService.getByPaging(accessToken, page_number, page_size);

        return writingList.stream()
                .map(Writing::getStory)
                .map(StoryMapper::toStoryInfoResponseDto)
                .collect(Collectors.toList());
    }

    public List<StoryResponse.StoryInfoResponseDTO> getAll(String accessToken) {
        List<Writing> writingList = writingService.getAll(accessToken);

        return writingList.stream()
                .map(Writing::getStory)
                .map(StoryMapper::toStoryInfoResponseDto)
                .collect(Collectors.toList());
    }

    public void update(Long storyId, StoryRequest.StoryUpdateRequestDTO storyUpdateRequestDto) {
        Story story = storyRepository.findById(storyId).orElse(null);
        if (story == null)
            throw new StoryHandler(ErrorStatus._STORY_NOT_FOUND);
        story.update(storyUpdateRequestDto);
        storyRepository.save(story);
    }

    public void delete(Long storyId) {
        Story story = storyRepository.findById(storyId).orElse(null);
        if (story == null)
            throw new StoryHandler(ErrorStatus._STORY_NOT_FOUND);
        storyRepository.delete(story);
    }
}
