package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.User;
import travel.travel.domain.UserImage;

import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    Optional<UserImage> findByUser(User user);
}
