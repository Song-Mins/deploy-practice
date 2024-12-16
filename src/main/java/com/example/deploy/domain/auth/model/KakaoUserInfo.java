package com.example.deploy.domain.auth.model;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;

@Getter
public class KakaoUserInfo {
    private final String name;
    private final String email;
    private final String profileImage;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.name = getNicknameByAttributes(attributes);
        this.email = getEmailByAttributes(attributes);
        this.profileImage = (String) attributes.get("profile_image");
    }

    public String getEmailByAttributes(Map<String, Object> attributes) {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReferencer = new TypeReference<>() {};

        Object kakaoAccount = attributes.get("kakao_account");
        Map<String, Object> account = objectMapper.convertValue(kakaoAccount, typeReferencer);
        if (!Objects.isNull(account)) {
            return (String) account.get("email");
        }
        return "";
    }

    public String getNicknameByAttributes(Map<String, Object> attributes) {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReferencer = new TypeReference<>() {};

        Object kakaoAccount = attributes.get("kakao_account");
        Map<String, Object> account = objectMapper.convertValue(kakaoAccount, typeReferencer);
        if (!Objects.isNull(account)) {
            if (!Objects.isNull(account.get("profile"))) {
                Map<String, Object> profile =
                        objectMapper.convertValue(account.get("profile"), typeReferencer);

                String nickname = (String) profile.get("nickname");
                return nickname;
            }
        }

        return "";
    }
}
