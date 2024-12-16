package com.example.deploy.domain.campaign.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    FOOD("맛집"),
    BEAUTY("뷰티"),
    TRAVEL("여행"),
    CULTURE("문화"),
    GROCERY("식품"),
    LIFESTYLE("생활"),
    DIGITAL("디지털");

    private final String displayName;
}
