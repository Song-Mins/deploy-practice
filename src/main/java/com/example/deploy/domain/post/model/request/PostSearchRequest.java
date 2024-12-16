package com.example.deploy.domain.post.model.request;


import com.example.deploy.domain.post.model.entity.enums.CommunityType;
import com.example.deploy.domain.post.model.entity.enums.FollowType;

public record PostSearchRequest(
	CommunityType communityType, FollowType followType, String keyword) {}
