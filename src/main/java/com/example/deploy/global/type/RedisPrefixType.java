package com.example.deploy.global.type;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RedisPrefixType {
    DEFAULT("default:"),
    POST_VIEW_COUNT("postViewCount:"),
    POST_COMMENT_COUNT("postCommentCount:");

    private final String prefix;

    @Override
    public String toString() {
        return prefix;
    }
}
