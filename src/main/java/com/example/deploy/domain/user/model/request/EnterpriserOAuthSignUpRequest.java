package com.example.deploy.domain.user.model.request;


import com.example.deploy.domain.user.model.entity.enums.OAuthType;

public record EnterpriserOAuthSignUpRequest(
        String email,
        String name,
        String nickname,
        String joinPath,
        String company,
        Boolean terms,
        Boolean personalInformation,
        Boolean marketing,
        String impId,
        OAuthType type) {}
