package com.example.deploy.domain.auth.client;


import com.example.deploy.domain.auth.model.KakaoUserInfo;
import com.example.deploy.domain.auth.model.TokenResponse;
import com.example.deploy.domain.user.exception.AuthException;
import com.example.deploy.domain.user.exception.errortype.AuthErrorCode;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class KakaoApiClient {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}") private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") private String redirectUri;

    private final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate = new RestTemplate();

    public TokenResponse getKakaoToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        //        params.add("client_secret", clientSecret);
        System.out.println(params);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<TokenResponse> response =
                restTemplate.postForEntity(TOKEN_URI, request, TokenResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new AuthException(AuthErrorCode.OAUTH_TOKEN_ISSUANCE_REQUEST_FAILED);
        }
    }

    public KakaoUserInfo getKakaoUserInfo(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(USER_INFO_URI, HttpMethod.GET, entity, Map.class);

        Map<String, Object> attributes = response.getBody();
        return new KakaoUserInfo(attributes);
    }
}
