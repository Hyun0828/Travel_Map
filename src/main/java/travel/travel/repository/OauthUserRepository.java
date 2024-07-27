package travel.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.oauth.OauthId;
import travel.travel.domain.oauth.OauthUser;

import java.util.Optional;

public interface OauthUserRepository extends JpaRepository<OauthUser, Long> {

    Optional<OauthUser> findByOauthId(OauthId oauthId);
}