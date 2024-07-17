package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.User;
import travel.travel.domain.UserImage;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    UserImage findByUser(User user);
}
