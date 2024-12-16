package com.example.deploy.domain.user.model.request;

public record EnterpriserSignUpRequest(
        String email,
        String password,
        String name,
        String nickname,
        String joinPath,
        String company,
        Boolean terms,
        Boolean personalInformation,
        Boolean marketing,
        String impId) {}
