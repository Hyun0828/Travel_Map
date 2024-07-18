package travel.travel.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.RefreshToken;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByRefresh(String refresh);

    Optional<RefreshToken> findByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}
