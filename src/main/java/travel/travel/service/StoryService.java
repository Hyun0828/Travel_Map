package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.domain.Story;
import travel.travel.dto.story.StoryCreateRequestDto;
import travel.travel.dto.story.StoryInfoResponseDto;
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

    public Long create(String accessToken, StoryCreateRequestDto storyCreateRequestDto) {
        Story story = StoryMapper.toStoryFromStoryCreateRequestDto(storyCreateRequestDto);
        Story savedStory = storyRepository.save(story);
        return savedStory.getId();
    }

    public StoryInfoResponseDto upload(Long storyId) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new NullPointerException("해당 일기가 없습니다"));
        return StoryMapper.toStoryInfoResponseDto(story);
    }

    public List<StoryInfoResponseDto> getAll(String accessToken) {
        List<Long> storyIdList = writingService.getAllStoryId(accessToken);

        return storyRepository.findStoriesByIdsOrdered(storyIdList).stream()
                .map(StoryMapper::toStoryInfoResponseDto)
                .collect(Collectors.toList());
    }
}
