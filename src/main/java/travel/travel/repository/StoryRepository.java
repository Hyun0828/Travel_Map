package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.travel.domain.Story;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {

}

