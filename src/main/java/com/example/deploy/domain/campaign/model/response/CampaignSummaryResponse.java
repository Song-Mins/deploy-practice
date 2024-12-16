package com.example.deploy.domain.campaign.model.response;


import com.example.deploy.domain.campaign.model.entity.Campaign;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;

public record CampaignSummaryResponse(
        /*체험단 목록 조회 시 반환할 데이터*/
        Long id, // 체험단 ID
        String businessName, // 상호명
        String imageUrl, // 이미지 URL
        String serviceProvided, // 보상
        Integer currentApplicants, // 현재 신청 인원
        Integer capacity, // 최대 신청 인원
        String campaignState, // 체험단 상태 (예: 모집중, 검수중 등)
        Integer pointPerPerson, // 1인당 지급 포인트
        String city, // 시/도
        String district, // 구/군
        String type, // 체험단 유형
        String label, // 라벨
        String platform, // 플랫폼
        LocalDateTime experienceStartDate, // 체험 시작일
        LocalDateTime experienceEndDate, // 체험 종료일
        Long applicationDeadline, // 지원 마감까지 남은 일수
        Boolean isCancellable, // 취소 가능한지 여부
        Boolean isLike // 좋아요 눌렀는지 여부
        ) {

    public static CampaignSummaryResponse from(Campaign campaign, Long userId) {

        // 지원 마감까지 남은 일수
        Long applicationDeadline = campaign.calculateApplicationDeadline();

        // 취소 가능 여부
        Boolean isCancellable = campaign.isCancelable();

        // 사용자가 좋아요 누른 캠페인인지
        Boolean isLike = campaign.isLike(userId);

        return new CampaignSummaryResponse(
                campaign.getId(),
                campaign.getBusinessName(),
                campaign.getImageUrl(),
                campaign.getServiceProvided(),
                campaign.getCurrentApplicants(),
                campaign.getCapacity(),
                campaign.getCampaignState().getDisplayName(),
                campaign.getPointPerPerson(),
                campaign.getCity(),
                campaign.getDistrict(),
                campaign.getType().getDisplayName(),
                campaign.getLabel().getDisplayName(),
                campaign.getPlatform().getDisplayName(),
                campaign.getExperienceStartDate(),
                campaign.getExperienceEndDate(),
                applicationDeadline,
                isCancellable,
                isLike);
    }

    public static Page<CampaignSummaryResponse> from(Page<Campaign> campaignPage, Long userId) {
        return campaignPage.map(campaign -> from(campaign, userId));
    }

    public static List<CampaignSummaryResponse> from(List<Campaign> campaignList, Long userId) {
        return campaignList.stream().map(campaign -> from(campaign, userId)).toList();
    }
}
