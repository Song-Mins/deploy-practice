package com.example.deploy.domain.user.model.response;


import com.example.deploy.domain.user.model.entity.Sns;
import com.example.deploy.domain.user.model.entity.enums.SnsType;

public record SnsResponse(SnsType snsType, String url) {

    public static SnsResponse from(Sns sns) {
        return new SnsResponse(sns.getSnsType(), sns.getUrl());
    }
}
