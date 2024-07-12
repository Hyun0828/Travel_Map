package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
