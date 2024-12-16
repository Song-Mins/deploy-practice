package com.example.deploy.domain.post.model.request;


import com.example.deploy.domain.post.model.entity.enums.CategoryType;
import com.example.deploy.domain.post.model.entity.enums.CommunityType;
import com.example.deploy.domain.post.model.entity.enums.FollowType;
import java.util.List;

public record PostRequest(
        String title,
        String content,
        CategoryType categoryType,
        CommunityType communityType,
        FollowType followType,
        List<String> deletedAttachedFiles) {}
