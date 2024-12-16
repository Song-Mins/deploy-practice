package com.example.deploy.global.type;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum S3PathPrefixType {
    S3_COMMUNITY_PATH("/post/community"),
    S3_FOLLOW_PATH("/post/follow"),
    S3_NOTICE_PATH("/post/notice"),
    S3_CAMPAIGN_THUMBNAIL_PATH("/campaign-thumbnail"),
    S3_PROFILE_IMAGE_PATH("/profile-image"),
    S3_REVIEW_IMAGE_PATH("/review-capture");

    private final String prefix;

    @Override
    public String toString() {
        return prefix;
    }
}
