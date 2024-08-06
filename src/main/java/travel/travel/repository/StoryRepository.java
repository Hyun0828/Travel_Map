package travel.travel.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.travel.domain.Story;
import travel.travel.domain.StoryImage;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {


}

