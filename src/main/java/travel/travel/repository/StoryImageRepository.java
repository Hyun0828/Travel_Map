package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.StoryImage;

public interface StoryImageRepository extends JpaRepository<StoryImage, Long> {
}
