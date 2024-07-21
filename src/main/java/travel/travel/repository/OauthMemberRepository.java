package travel.travel.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.domain.oauth.OauthId;
import travel.travel.domain.oauth.OauthUser;

public interface OauthMemberRepository extends JpaRepository<OauthUser, Long> {

    Optional<OauthUser> findByOauthId(OauthId oauthId);
}