package com.example.deploy.domain.campaign.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CampaignState {
    INSPECTION("검수증"),
    RECRUITING("모집중"),
    RECRUITMENT_COMPLETED("모집완료"),
    EXPERIENCE_AND_REVIEW("체험&리뷰"),
    REVIEW_CLOSED("리뷰마감");

    private final String displayName;
}
