package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
