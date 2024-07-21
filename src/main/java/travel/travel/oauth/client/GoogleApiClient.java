package travel.travel.oauth.client;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import travel.travel.oauth.dto.GoogleMemberResponse;
import travel.travel.oauth.dto.GoogleToken;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

public interface GoogleApiClient {


    @PostExchange(url = "https://oauth2.googleapis.com/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    GoogleToken fetchToken(@RequestParam MultiValueMap<String, String> params);

    @GetExchange("https://www.googleapis.com/userinfo/v2/me")
    GoogleMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);
}
