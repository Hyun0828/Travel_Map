package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.CommentImage;

public interface CommentImageRepository extends JpaRepository<CommentImage, Long> {
}
