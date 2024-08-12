package travel.travel.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.Story;
import travel.travel.domain.User;
import travel.travel.domain.Writing;

import java.util.List;

public interface WritingRepository extends JpaRepository<Writing, Long> {

    List<Writing> findAllByWriter(User writer);

    Page<Writing> findAllByWriter(User writer, Pageable pageable);

    @Transactional
    void deleteByStory(Story story);
}
