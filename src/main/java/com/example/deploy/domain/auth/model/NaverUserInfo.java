package com.example.deploy.domain.auth.model;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.Getter;

@Getter
public class NaverUserInfo {
    private final String name;
    private final String email;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.email = getEmailByAttributes(attributes);
        this.name = getNameByAttributes(attributes);
    }

    public String getEmailByAttributes(Map<String, Object> attributes) {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReferencer = new TypeReference<>() {};

        Object naverResponse = attributes.get("response");

        Map<String, Object> response = objectMapper.convertValue(naverResponse, typeReferencer);

        return (String) response.get("email");
    }

    public String getNameByAttributes(Map<String, Object> attributes) {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReferencer = new TypeReference<>() {};

        Object naverResponse = attributes.get("response");
        Map<String, Object> response = objectMapper.convertValue(naverResponse, typeReferencer);

        return (String) response.get("name");
    }
}
