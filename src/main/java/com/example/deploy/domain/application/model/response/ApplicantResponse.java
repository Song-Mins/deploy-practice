package com.example.deploy.domain.application.model.response;


import com.example.deploy.domain.application.model.entity.Application;
import com.example.deploy.domain.campaign.exception.CampaignException;
import com.example.deploy.domain.campaign.exception.errortype.CampaignErrorCode;
import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import java.util.List;

public record ApplicantResponse(
        Long userId,
        String name,
        Boolean isChoice, // 선정됐는지 여부
        Long cancelledApplicationCount, // 취소한 신청수
        String message, // 신청 한마디
        Integer capacity // 모집 인원
        ) {

    public static List<ApplicantResponse> of(Campaign campaign, Long userId) {

        // 캠페인의 유저아이디와 로그인 유저 아이디 비교
        if (!userId.equals(campaign.getUser().getId())) {
            throw new CampaignException(CampaignErrorCode.CANNOT_MANAGE_CAMPAIGN);
        }
        // 검수중, 리뷰마감 단계가 아닌지 확인
        if (CampaignState.REVIEW_CLOSED.equals(campaign.getCampaignState())
                || CampaignState.INSPECTION.equals(campaign.getCampaignState())) {
            throw new CampaignException(CampaignErrorCode.CANNOT_MANAGE_CAMPAIGN);
        }
        return campaign.getApplicationList().stream().map(ApplicantResponse::of).toList();
    }

    public static ApplicantResponse of(Application application) {

        // 선정됐는지 여부
        Long userId = application.getUser().getId();
        Boolean selected =
                application.getCampaign().getChoiceList().stream()
                        .anyMatch(select -> userId.equals(select.getUser().getId()));

        // 취소한 캠페인 수
        Long cancelledApplicationCount =
                application.getUser().getApplicationList().stream()
                        .filter(Application::getIsDeleted)
                        .count();

        return new ApplicantResponse(
                application.getUser().getId(),
                application.getUser().getName(),
                selected,
                cancelledApplicationCount,
                application.getMessage(),
                application.getCampaign().getCapacity());
    }
}
