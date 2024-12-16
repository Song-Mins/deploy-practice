package com.example.deploy.domain.campaign.model.response;


import com.example.deploy.domain.campaign.model.entity.AvailableDay;
import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.model.entity.Keyword;
import com.example.deploy.domain.user.model.entity.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

public record CampaignResponse( // 승인된 체험단 반환
        Long id,
        String businessName,
        String imageUrl,
        String contactNumber,
        String address,
        String addressDetail,
        Integer postalCode,
        String city,
        String district,
        Set<String> availableDays,
        String type,
        String category,
        String platform,
        String label,
        Integer capacity,
        Integer currentApplicants,
        String serviceProvided,
        String[] requirement,
        Set<String> keywords,
        Boolean pointPayment,
        Integer pointPerPerson,
        Integer totalPoints,
        Boolean isLike, // 좋아요 눌렀는지 여부
        Long applicationDeadline,
        LocalDate applicationStartDate,
        LocalDate applicationEndDate,
        LocalDate announcementDate,
        LocalDate experienceStartDate,
        LocalDate experienceEndDate,
        LocalTime experienceStartTime,
        LocalTime experienceEndTime,
        String serviceUrl,

        // 추가된 사업주 정보
        Long enterpriserId, // 사업주 고유 ID
        String enterpriserProfileImage, // 사업주 프로필 이미지 URL
        String enterpriserCompanyName // 사업주 업체명
        ) {

    public static CampaignResponse from(Campaign campaign, Long userId) {

        // 지원 마감까지 남은 일수
        Long applicationDeadline = campaign.calculateApplicationDeadline();

        // 사업주 요청사항을 엔터 기준으로 배열로 변환 (임시로)
        String[] requirementArray =
                campaign.getRequirement() != null
                        ? campaign.getRequirement().split("\n")
                        : new String[] {};

        // 사용자가 좋아요 누른 캠페인인지
        Boolean isLike = campaign.isLike(userId);

        // 사업주 정보 가져오기
        User enterpriser = campaign.getUser();
        String enterpriserProfileImage = enterpriser.getProfileImageUrl();
        String enterpriserCompanyName = enterpriser.getEnterpriser().getCompany();
        Long enterpriserId = enterpriser.getId();

        return new CampaignResponse(
                campaign.getId(),
                campaign.getBusinessName(),
                campaign.getImageUrl(),
                campaign.getContactNumber(),
                campaign.getAddress(),
                campaign.getAddressDetail(), // 상세 주소 추가
                campaign.getPostalCode(),
                campaign.getCity(),
                campaign.getDistrict(),
                campaign.getAvailableDays().stream()
                        .map(AvailableDay::getDay)
                        .collect(Collectors.toSet()),
                campaign.getType().getDisplayName(),
                campaign.getCategory().getDisplayName(),
                campaign.getPlatform().getDisplayName(),
                campaign.getLabel().getDisplayName(),
                campaign.getCapacity(),
                campaign.getCurrentApplicants(),
                campaign.getServiceProvided(),
                requirementArray,
                campaign.getKeywords().stream()
                        .map(Keyword::getKeyword)
                        .collect(Collectors.toSet()),
                campaign.getPointPayment(),
                campaign.getPointPerPerson(),
                campaign.getTotalPoints(),
                isLike,
                applicationDeadline,
                campaign.getApplicationStartDate().toLocalDate(),
                campaign.getApplicationEndDate().toLocalDate(),
                campaign.getAnnouncementDate().toLocalDate(),
                campaign.getExperienceStartDate().toLocalDate(),
                campaign.getExperienceEndDate().toLocalDate(),
                campaign.getExperienceStartTime(),
                campaign.getExperienceEndTime(),
                campaign.getServiceUrl(),
                enterpriserId,
                enterpriserProfileImage,
                enterpriserCompanyName);
    }
}
