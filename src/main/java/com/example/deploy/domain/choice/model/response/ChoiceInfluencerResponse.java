package com.example.deploy.domain.choice.model.response;


import com.example.deploy.domain.application.model.entity.Application;
import com.example.deploy.domain.campaign.exception.CampaignException;
import com.example.deploy.domain.campaign.exception.errortype.CampaignErrorCode;
import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import java.util.List;

public record ChoiceInfluencerResponse(String name, String phone, String message) {

    public static List<ChoiceInfluencerResponse> of(Campaign campaign, Long userId) {
        // 캠페인의 유저아이디와 로그인 유저 아이디 비교
        if (!userId.equals(campaign.getUser().getId())) {
            throw new CampaignException(CampaignErrorCode.CANNOT_MANAGE_CAMPAIGN);
        }
        // 모집완료 단계인지 확인
        if (!CampaignState.RECRUITMENT_COMPLETED.equals(campaign.getCampaignState())) {
            throw new CampaignException(CampaignErrorCode.CANNOT_MANAGE_CAMPAIGN);
        }

        return campaign.getApplicationList().stream().map(ChoiceInfluencerResponse::of).toList();
    }

    public static ChoiceInfluencerResponse of(Application application) {

        return new ChoiceInfluencerResponse(
                application.getUser().getName(),
                application.getUser().getPhone(),
                application.getMessage());
    }
}
