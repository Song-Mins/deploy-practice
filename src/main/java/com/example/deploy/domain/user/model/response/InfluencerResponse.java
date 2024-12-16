package com.example.deploy.domain.user.model.response;


import com.example.deploy.domain.application.model.entity.Application;
import com.example.deploy.domain.application.model.entity.enums.ApplicationState;
import com.example.deploy.domain.user.model.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public record InfluencerResponse(
        String nickname,
        String profileImageUrl,
        List<SnsResponse> snsResponseList,
        Integer likeCount,
        Long appliedCampaignCount, // 신청한 캠페인 수
        Long selectedCampaignCount, // 선정된 캠페인 수
        Long ongoingCampaignCount, // 진행중인 캠페인 수
        Long cancelledApplicationCount // 취소한 신청 수
        ) {

    public static InfluencerResponse from(User user) {

        // 사용자가 좋아요한 캠페인 수 구하기
        int likeCnt = user.getCampaignList().size();

        // List<Sns> -> List<SnsResponse>
        List<SnsResponse> snsResponseList =
                user.getInfluencer().getSnsList().stream().map(SnsResponse::from).toList();

        // 신청한 캠페인 수 구하기 - 취소는 포함안함
        Long appliedCampaignCount =
                user.getApplicationList().stream()
                        .filter(application -> !application.getIsDeleted())
                        .count();

        // 선정된 캠페인 수
        Long selectedCampaignCount =
                user.getApplicationList().stream()
                        .filter(
                                application ->
                                        ApplicationState.APPROVED.equals(
                                                application.getApplicationState()))
                        .count();

        // 진행중인 캠페인 수
        Long ongoingCampaignCount =
                user.getApplicationList().stream()
                        .filter(
                                application ->
                                        LocalDateTime.now()
                                                        .isAfter(
                                                                application
                                                                        .getCampaign()
                                                                        .getExperienceStartDate())
                                                && LocalDateTime.now()
                                                        .isBefore(
                                                                application
                                                                        .getCampaign()
                                                                        .getExperienceEndDate()))
                        .count();

        // 취소한 캠페인 수
        Long cancelledApplicationCount =
                user.getApplicationList().stream().filter(Application::getIsDeleted).count();

        return new InfluencerResponse(
                user.getNickname(),
                user.getProfileImageUrl(),
                snsResponseList,
                likeCnt,
                appliedCampaignCount,
                selectedCampaignCount,
                ongoingCampaignCount,
                cancelledApplicationCount);
    }
}
