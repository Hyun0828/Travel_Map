package travel.travel.oauth;


/**
 * AuthCode를 발급하는 URL을 제공하는 인터페이스
 */

public interface AuthCodeRequestUrlProvider {

    /**
     * 자신이 어떤 OauthServerType을 지원할 수 있는지
     */
    OauthServerType supportServer();

    /**
     * URL을 생성 후 반환한다.
     */
    String provide();
}
