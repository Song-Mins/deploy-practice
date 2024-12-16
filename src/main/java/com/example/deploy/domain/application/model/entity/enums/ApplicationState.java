package com.example.deploy.domain.application.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationState {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("거부");

    private final String displayName;
}
