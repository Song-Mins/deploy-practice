package com.example.deploy.domain.user.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SnsType {
    NAVER_BLOG("네이버 블로그"),
    INSTAGRAM("인스타그램"),
    YOUTUBE("유튜브"),
    TIKTOK("틱톡"),
    ETC("기타");

    private final String displayName;
}
