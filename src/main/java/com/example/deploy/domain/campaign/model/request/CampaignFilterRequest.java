package com.example.deploy.domain.campaign.model.request;


import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import com.example.deploy.domain.campaign.model.entity.enums.Platform;

// null 값이 들어와도 상관없음.
public record CampaignFilterRequest(
	Platform platform, CampaignState campaignState, String keyword) {}
