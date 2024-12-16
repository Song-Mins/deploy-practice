package com.example.deploy.domain.application.model.response;


import com.example.deploy.domain.application.model.entity.Application;
import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import com.example.deploy.domain.campaign.model.entity.enums.Label;
import com.example.deploy.domain.campaign.model.entity.enums.Platform;
import com.example.deploy.domain.campaign.model.entity.enums.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public record ApplicationCampaignResponse(
        /*신청자의 관점의 캠페인 정보*/
        Long id,
        String nickname,
        String serviceProvided,
        String city,
        String district,
        Type type,
        Platform platform,
        String imageUrl,
        CampaignState campaignState,
        Integer capacity,
        Integer applicant,
        LocalDateTime experienceStartDate,
        LocalDateTime experienceEndDate,
        Long applicationDeadline, // 지원 마감까지 남은 일수
        Boolean isCancel, // 취소 가능한지 여부
        Label label) {

    public static ApplicationCampaignResponse from(Application application) {

        // 지원 마감까지 남은 일수 계산
        LocalDateTime now = LocalDateTime.now();
        Duration duration =
                Duration.between(now, application.getCampaign().getApplicationEndDate());
        Long applicationDeadline = Math.max(duration.toDays(), 0);

        // 취소 가능 여부 판단 - 리뷰 종료 단계가 아닌 경우
        Boolean isCancel =
                !CampaignState.REVIEW_CLOSED.equals(application.getCampaign().getCampaignState());

        // CampaignToResponse 객체로 변환
        return new ApplicationCampaignResponse(
                application.getCampaign().getId(),
                application.getCampaign().getUser().getNickname(),
                application.getCampaign().getServiceProvided(),
                application.getCampaign().getCity(),
                application.getCampaign().getDistrict(),
                application.getCampaign().getType(),
                application.getCampaign().getPlatform(),
                application.getCampaign().getUser().getProfileImage(),
                application.getCampaign().getCampaignState(),
                application.getCampaign().getCapacity(),
                application.getCampaign().getCurrentApplicants(),
                application.getCampaign().getExperienceStartDate(),
                application.getCampaign().getExperienceEndDate(),
                applicationDeadline,
                isCancel,
                application.getCampaign().getLabel());
    }
}
