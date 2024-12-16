package com.example.deploy.domain.campaign.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Platform {
    BLOG("블로그"),
    INSTAGRAM("인스타그램"),
    YOUTUBE("유튜브"),
    TIKTOK("틱톡"),
    REELS("릴스"),
    SHORTS("쇼츠");

    private final String displayName;
}
