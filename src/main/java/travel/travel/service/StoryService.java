package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.domain.Story;
import travel.travel.domain.Writing;
import travel.travel.dto.story.StoryCreateRequestDto;
import travel.travel.dto.story.StoryInfoResponseDto;
import travel.travel.dto.story.StoryUpdateRequestDto;
import travel.travel.mapper.StoryMapper;
import travel.travel.repository.StoryRepository;
import travel.travel.repository.WritingRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final WritingService writingService;
    private final WritingRepository writingRepository;

    public Long create(String accessToken, StoryCreateRequestDto storyCreateRequestDto) {
        Story story = StoryMapper.toStoryFromStoryCreateRequestDto(storyCreateRequestDto);
        Story savedStory = storyRepository.save(story);
        return savedStory.getId();
    }

    public StoryInfoResponseDto upload(Long storyId) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new NullPointerException("해당 일기가 없습니다"));
        return StoryMapper.toStoryInfoResponseDto(story);
    }

    public List<StoryInfoResponseDto> getByPaging(String accessToken, Integer page_number, Integer page_size) {
        List<Writing> writingList = writingService.getByPaging(accessToken, page_number, page_size);

        return writingList.stream()
                .map(Writing::getStory)
                .map(StoryMapper::toStoryInfoResponseDto)
                .collect(Collectors.toList());
    }

    public List<StoryInfoResponseDto> getAll(String accessToken) {
        List<Writing> writingList = writingService.getAll(accessToken);

        return writingList.stream()
                .map(Writing::getStory)
                .map(StoryMapper::toStoryInfoResponseDto)
                .collect(Collectors.toList());
    }

    public void update(Long storyId, StoryUpdateRequestDto storyUpdateRequestDto) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new NullPointerException("해당 일기가 없습니다"));
        story.update(storyUpdateRequestDto);
        storyRepository.save(story);
    }

    public void delete(Long storyId) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new NullPointerException("해당 일기가 없습니다"));
        storyRepository.delete(story);
    }
}
