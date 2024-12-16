package com.example.deploy.domain.user.model.request;


import com.example.deploy.domain.user.model.entity.enums.SnsType;

public record SingUpSns(SnsType snsType, String url) {}
