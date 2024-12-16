package com.example.deploy.domain.post.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowType {
    BLOG("블로그"),
    INSTAGRAM("인스타그램"),
    YOUTUBE("유튜브"),
    TIKTOK("틱톡"),
    ETC("기타");

    private final String displayName;
}
