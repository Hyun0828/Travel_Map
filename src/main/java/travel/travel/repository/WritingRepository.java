package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.travel.domain.User;
import travel.travel.domain.Writing;

import java.util.List;

public interface WritingRepository extends JpaRepository<Writing, Long> {

    @Query("SELECT w.story.id FROM Writing w WHERE w.writer = :user")
    List<Long> findStoryIdsByUser(@Param("user") User user);
}
