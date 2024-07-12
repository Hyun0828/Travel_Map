package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.Writing;

public interface WritingRepository extends JpaRepository<Writing, Long> {
}
