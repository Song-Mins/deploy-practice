package com.example.deploy.domain.admin.model.request;


import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CampaignApprovalRequest(
        @NotNull LocalDateTime applicationStartDate, // 신청 시작일
        @NotNull LocalDateTime applicationEndDate, // 신청 마감일
        @NotNull LocalDateTime announcementDate, // 선정자 발표일
        @NotNull LocalDateTime experienceStartDate, // 체험 시작일
        @NotNull LocalDateTime experienceEndDate // 체험 종료일
        ) {}
