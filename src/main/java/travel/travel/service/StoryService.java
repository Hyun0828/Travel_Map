package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.repository.StoryRepository;

@Transactional
@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;


}
