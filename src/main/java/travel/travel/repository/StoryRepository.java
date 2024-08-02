package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.travel.domain.Story;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {

    @Query("SELECT s FROM Story s WHERE s.id IN :ids ORDER BY s.id ASC")
    List<Story> findStoriesByIdsOrdered(@Param("ids") List<Long> ids);
}

