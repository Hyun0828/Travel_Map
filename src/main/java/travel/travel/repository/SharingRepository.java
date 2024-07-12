package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.Sharing;

public interface SharingRepository extends JpaRepository<Sharing, Long> {
}
