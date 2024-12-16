package com.example.deploy.domain.user.model.request;


import com.example.deploy.domain.user.model.entity.enums.OAuthType;

public record OAuthInfoRequest(String code, OAuthType type) {}
