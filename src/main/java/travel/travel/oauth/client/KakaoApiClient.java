package travel.travel.oauth.client;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import travel.travel.oauth.dto.KakaoMemberResponse;
import travel.travel.oauth.dto.KakaoToken;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

/**
 * Http Interface Client를 사용한 코드
 * Url : AccessToken을 받아오기 위한 URL, 이를 위한 Content-type, 요청 파라미터를 받는다.
 * 응답값은 KakaoToken을 통해 객체로 바로 받아올 수 있게
 * <p>
 * 그 밑은 accessToken으로 사용자 정보를 받아오기 위한 메소드
 */

public interface KakaoApiClient {

    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoToken fetchToken(@RequestParam MultiValueMap<String, String> params);

    @GetExchange("https://kapi.kakao.com/v2/user/me")
    KakaoMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);
}
