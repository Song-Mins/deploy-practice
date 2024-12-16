package com.example.deploy.domain.auth.client;


import com.example.deploy.domain.auth.model.NaverUserInfo;
import com.example.deploy.domain.auth.model.TokenResponse;
import com.example.deploy.domain.user.exception.AuthException;
import com.example.deploy.domain.user.exception.errortype.AuthErrorCode;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class NaverApiClient {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}") private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}") private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}") private String redirectUri;

    private final String TOKEN_URI = "https://nid.naver.com/oauth2.0/token";
    private final String USER_INFO_URI = "https://openapi.naver.com/v1/nid/me";

    private final RestTemplate restTemplate = new RestTemplate();

    public TokenResponse getNaverToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<TokenResponse> response =
                restTemplate.postForEntity(TOKEN_URI, request, TokenResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new AuthException(AuthErrorCode.OAUTH_TOKEN_ISSUANCE_REQUEST_FAILED);
        }
    }

    public NaverUserInfo getNaverUserInfo(String naverAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + naverAccessToken);
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(USER_INFO_URI, HttpMethod.GET, entity, Map.class);

        Map<String, Object> attributes = response.getBody();
        return new NaverUserInfo(attributes);
    }
}
