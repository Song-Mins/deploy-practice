package com.example.deploy.domain.admin.model.response;


import com.example.deploy.domain.campaign.model.entity.Campaign;
import java.time.LocalDateTime;

public record CampaignApprovalResponse(
        Long campaignId,
        LocalDateTime applicationStartDate,
        LocalDateTime applicationEndDate,
        LocalDateTime announcementDate,
        LocalDateTime experienceStartDate,
        LocalDateTime experienceEndDate,
        LocalDateTime approvedDate,
        String campaignState) {

    public static CampaignApprovalResponse from(Campaign campaign) {
        return new CampaignApprovalResponse(
                campaign.getId(),
                campaign.getApplicationStartDate(),
                campaign.getApplicationEndDate(),
                campaign.getAnnouncementDate(),
                campaign.getExperienceStartDate(),
                campaign.getExperienceEndDate(),
                campaign.getApprovedDate(),
                campaign.getCampaignState().getDisplayName());
    }
}
