package com.example.deploy.domain.user.service;


import com.example.deploy.domain.auth.client.GoogleApiClient;
import com.example.deploy.domain.auth.client.KakaoApiClient;
import com.example.deploy.domain.auth.client.NaverApiClient;
import com.example.deploy.domain.user.exception.RegisterException;
import com.example.deploy.domain.user.exception.errortype.RegisterErrorCode;
import com.example.deploy.domain.user.model.entity.enums.OAuthType;
import com.example.deploy.domain.user.model.request.ImpInfoRequest;
import com.example.deploy.domain.user.model.request.OAuthInfoRequest;
import com.example.deploy.domain.user.model.response.ImpInfoResponse;
import com.example.deploy.domain.user.model.response.OAuthInfoResponse;
import com.example.deploy.global.api.API;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Certification;
import com.siot.IamportRestClient.response.IamportResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InfoService {

    private final KakaoApiClient kakaoApiClient;
    private final GoogleApiClient googleApiClient;
    private final NaverApiClient naverApiClient;
    private final IamportClient iamportClient;

    private String OAuthGetName(String code, OAuthType type) {
        return switch (type) {
            case KAKAO -> kakaoApiClient
                    .getKakaoUserInfo(kakaoApiClient.getKakaoToken(code).getAccessToken())
                    .getEmail();
            case NAVER -> naverApiClient
                    .getNaverUserInfo(naverApiClient.getNaverToken(code).getAccessToken())
                    .getEmail();
            case GOOGLE -> googleApiClient
                    .getGoogleUserInfo(googleApiClient.getGoogleToken(code).getAccessToken())
                    .getEmail();
            default -> throw new RegisterException(RegisterErrorCode.NOT_FOUND_OAUTH_TYPE);
        };
    }

    public ResponseEntity getOAuthInfo(OAuthInfoRequest request) {
        return API.OK(new OAuthInfoResponse(OAuthGetName(request.code(), request.type())));
    }

    public ResponseEntity getImpInfo(ImpInfoRequest request) {
        try {

            IamportResponse<Certification> certification =
                    iamportClient.certificationByImpUid(request.impId());

            return API.OK(new ImpInfoResponse(certification.getResponse().getName()));
        } catch (IamportResponseException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        } catch (IOException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        }
    }
}
