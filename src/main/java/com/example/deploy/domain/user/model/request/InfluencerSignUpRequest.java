package com.example.deploy.domain.user.model.request;


import java.util.List;

public record InfluencerSignUpRequest(
        String email,
        String password,
        String name,
        String nickname,
        List<SingUpSns> snsResponseList,
        String joinPath,
        Boolean terms,
        Boolean personalInformation,
        Boolean marketing,
        String impId) {}
