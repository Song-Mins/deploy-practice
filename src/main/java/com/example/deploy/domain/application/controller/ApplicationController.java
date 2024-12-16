package com.example.deploy.domain.application.controller;



import com.example.deploy.domain.application.model.request.ApplicationRequest;
import com.example.deploy.domain.application.model.response.ApplicationCampaignResponse;
import com.example.deploy.domain.application.service.ApplicationService;
import com.example.deploy.domain.campaign.model.request.CampaignFilterRequest;
import com.example.deploy.domain.user.config.model.CustomUserDetails;
import com.example.deploy.global.api.API;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/influencer/application")
public class ApplicationController {

    private final ApplicationService applicationService;

    // 체험단 신청
    @PostMapping
    public ResponseEntity<?> applyCampaign(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid ApplicationRequest applicationRequest) {

        applicationService.applyCampaign(applicationRequest, customUserDetails.getUserId());
        return API.OK();
    }

    // 체험단 신청 취소
    @PatchMapping("/{applicationId}")
    public ResponseEntity<?> cancelApplication(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long applicationId) {

        applicationService.cancelApplication(applicationId, customUserDetails.getUserId());
        return API.OK();
    }

    // 신청한 체험단 리스트 페이지네이션으로 가져오기
    @GetMapping("/me")
    public ResponseEntity<Page<ApplicationCampaignResponse>> getApplications(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute CampaignFilterRequest campaignFilterRequest,
            Pageable pageable) {

        Page<ApplicationCampaignResponse> response =
                applicationService.getApplications(
                        campaignFilterRequest, pageable, customUserDetails.getUserId());

        return API.OK(response);
    }
}
