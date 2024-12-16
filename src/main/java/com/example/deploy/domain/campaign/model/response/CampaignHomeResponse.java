package com.example.deploy.domain.campaign.model.response;


import java.util.List;

public record CampaignHomeResponse(
        List<CampaignSummaryResponse> premium,
        List<CampaignSummaryResponse> popular,
        List<CampaignSummaryResponse> newest,
        List<CampaignSummaryResponse> imminent) {

    public static CampaignHomeResponse of(
            List<CampaignSummaryResponse> from,
            List<CampaignSummaryResponse> from1,
            List<CampaignSummaryResponse> from2,
            List<CampaignSummaryResponse> from3) {
        return new CampaignHomeResponse(from, from1, from2, from3);
    }
}
