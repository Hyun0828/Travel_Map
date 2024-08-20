package travel.travel.oauth.client;


import travel.travel.domain.oauth.OauthUser;
import travel.travel.oauth.OauthServerType;

/**
 * AuthCode를 통해 OauthMember 객체 생성 (회원 정보 조회)
 */

public interface OauthUserClient {

    OauthServerType supportServer();

    OauthUser fetch(String code);
}