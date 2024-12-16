package com.example.deploy.domain.campaign.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortBy {
    RECOMMENDED("추천순"), // 추천순
    POPULAR("인기순"), // 인기순
    CLOSING_SOON("마감임박순"), // 마감임박순
    NEWEST("최신순"); // 최신순

    private final String displayName;
}
