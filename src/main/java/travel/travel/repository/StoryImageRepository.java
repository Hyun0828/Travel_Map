package travel.travel.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.StoryImage;

import java.util.List;

public interface StoryImageRepository extends JpaRepository<StoryImage, Long> {


    List<StoryImage> findByStoryId(Long storyId);

    @Transactional
    void deleteByStoryId(Long storyId);
}
