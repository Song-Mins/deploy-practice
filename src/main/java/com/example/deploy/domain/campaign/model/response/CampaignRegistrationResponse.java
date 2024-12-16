package com.example.deploy.domain.campaign.model.response;


import com.example.deploy.domain.campaign.model.entity.AvailableDay;
import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.model.entity.Keyword;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

public record CampaignRegistrationResponse( // 체험단 첫 등록시 반환받을 응답 내용
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
        LocalTime experienceStartTime,
        LocalTime experienceEndTime,
        String serviceUrl) {

    public static CampaignRegistrationResponse from(Campaign campaign) {

        // 사업주 요청사항을 엔터 기준으로 배열로 변환 (임시로)
        String[] requirementArray =
                campaign.getRequirement() != null
                        ? campaign.getRequirement().split("\n")
                        : new String[] {};

        return new CampaignRegistrationResponse(
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
                campaign.getExperienceStartTime(), // LocalTime 그대로 반환
                campaign.getExperienceEndTime(),
                campaign.getServiceUrl());
    }
}
