package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import travel.travel.domain.Story;
import travel.travel.domain.User;
import travel.travel.domain.Writing;
import travel.travel.jwt.service.JwtService;
import travel.travel.repository.StoryRepository;
import travel.travel.repository.UserRepository;
import travel.travel.repository.WritingRepository;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class WritingService {

    private final UserRepository userRepository;
    private final WritingRepository writingRepository;
    private final StoryRepository storyRepository;
    private final JwtService jwtService;

    public void save(String accessToken, Long storyId) {
        String email = jwtService.extractEmail(accessToken).orElseThrow(() -> new IllegalStateException("유효하지 않은 토큰입니다."));
        User writer = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("해당하는 사용자가 없습니다."));
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new NullPointerException("해당하는 일기가 없습니다"));
        Writing writing = Writing.builder()
                .writer(writer)
                .story(story)
                .build();

        writingRepository.save(writing);
    }

    public List<Writing> getByPaging(String accessToken, Integer page_number, Integer page_size) {
        String email = jwtService.extractEmail(accessToken).orElseThrow(() -> new IllegalStateException("유효하지 않은 토큰입니다."));
        User writer = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("해당하는 사용자가 없습니다."));

        Pageable pageable = PageRequest.of(page_number - 1, page_size);
        Page<Writing> pagedWritings = writingRepository.findAllByWriter(writer, pageable);

        return pagedWritings.getContent();
    }

    public List<Writing> getAll(String accessToken) {
        String email = jwtService.extractEmail(accessToken).orElseThrow(() -> new IllegalStateException("유효하지 않은 토큰입니다."));
        User writer = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("해당하는 사용자가 없습니다."));
        return writingRepository.findAllByWriter(writer);
    }

    public int count(String accessToken) {
        String email = jwtService.extractEmail(accessToken).orElseThrow(() -> new IllegalStateException("유효하지 않은 토큰입니다."));
        User writer = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("해당하는 사용자가 없습니다."));
        return writingRepository.findAllByWriter(writer).size();
    }

    public void delete(Long storyId) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new NullPointerException("해당하는 일기가 없습니다"));
        writingRepository.deleteByStory(story);
    }
}