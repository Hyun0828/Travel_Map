package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import travel.travel.apiPayload.code.status.ErrorStatus;
import travel.travel.apiPayload.exception.handler.AccessTokenHandler;
import travel.travel.apiPayload.exception.handler.StoryHandler;
import travel.travel.apiPayload.exception.handler.UserHandler;
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
        String email = jwtService.extractEmail(accessToken).orElse(null);
        if (email == null)
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            throw new UserHandler(ErrorStatus._USER_NOT_FOUND);
        Story story = storyRepository.findById(storyId).orElse(null);
        if (story == null)
            throw new StoryHandler(ErrorStatus._STORY_NOT_FOUND);
        Writing writing = Writing.builder()
                .writer(user)
                .story(story)
                .build();

        writingRepository.save(writing);
    }

    public List<Writing> getByPaging(String accessToken, Integer page_number, Integer page_size) {
        String email = jwtService.extractEmail(accessToken).orElse(null);
        if (email == null)
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            throw new UserHandler(ErrorStatus._USER_NOT_FOUND);

        Pageable pageable = PageRequest.of(page_number - 1, page_size);
        Page<Writing> pagedWritings = writingRepository.findAllByWriter(user, pageable);

        return pagedWritings.getContent();
    }

    public List<Writing> getAll(String accessToken) {
        String email = jwtService.extractEmail(accessToken).orElse(null);
        if (email == null)
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            throw new UserHandler(ErrorStatus._USER_NOT_FOUND);
        return writingRepository.findAllByWriter(user);
    }

    public int count(String accessToken) {
        String email = jwtService.extractEmail(accessToken).orElse(null);
        if (email == null)
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            throw new UserHandler(ErrorStatus._USER_NOT_FOUND);
        return writingRepository.findAllByWriter(user).size();
    }

    public void delete(Long storyId) {
        Story story = storyRepository.findById(storyId).orElse(null);
        if (story == null)
            throw new StoryHandler(ErrorStatus._STORY_NOT_FOUND);
        writingRepository.deleteByStory(story);
    }
}