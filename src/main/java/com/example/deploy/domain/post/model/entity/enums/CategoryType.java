package com.example.deploy.domain.post.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {
    NOTICE("공지사항"),
    COMMUNITY("커뮤니티"),
    FOLLOW("맞팔/서이추");

    private final String displayName;
}
