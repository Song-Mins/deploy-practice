package com.example.deploy.domain.campaign.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Label {
    PREMIUM("프리미엄"),
    DAIN_CAMPAIGN("다인체험단"),
    GENERAL_CAMPAIGN("일반체험단");

    private final String displayName;
}
