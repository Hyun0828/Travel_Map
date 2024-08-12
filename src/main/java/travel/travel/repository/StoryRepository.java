package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.Story;

public interface StoryRepository extends JpaRepository<Story, Long> {


}

