package com.example.deploy.domain.admin.model.response;


import com.example.deploy.domain.campaign.model.entity.Campaign;
import java.time.format.DateTimeFormatter;

public record AdminCampaignListResponse(
        String id, // 번호 - 체험단 ID
        String businessName, // 업체명
        String label, // 라벨
        Integer point, // 1인당 포인트
        Integer capacity, // 모집 인원
        String email, // 사업주 가입 시 아이디(이메일)
        String platform, // 플랫폼
        String type, // 유형
        String phone, // 전화번호
        String createdAt, // 체험단 등록일
        String campaignState // 체험단 상태
        ) {

    public static AdminCampaignListResponse from(Campaign campaign) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return new AdminCampaignListResponse(
                String.valueOf(campaign.getId()), // 체험단 ID
                campaign.getBusinessName(), // 업체명
                campaign.getLabel().getDisplayName(), // 라벨
                campaign.getPointPerPerson(), // 1인당 포인트
                campaign.getCapacity(), // 모집 인원
                campaign.getUser().getEmail(), // 사업주 가입 시 아이디(이메일)
                campaign.getPlatform().getDisplayName(), // 플랫폼
                campaign.getType().getDisplayName(), // 유형
                campaign.getUser().getPhone(), // 휴대전화 번호
                formatter.format(campaign.getCreatedAt()), // 생성일
                campaign.getCampaignState().getDisplayName() // 체험단 상태
                );
    }
}
