package com.example.deploy.domain.admin.controller;



import com.example.deploy.domain.admin.model.request.CampaignApprovalRequest;
import com.example.deploy.domain.admin.model.response.CampaignApprovalResponse;
import com.example.deploy.domain.admin.service.AdminService;
import com.example.deploy.domain.campaign.model.response.CampaignRegistrationResponse;
import com.example.deploy.global.api.API;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getUnapprovedCampaigns(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return adminService.getUnapprovedCampaigns(page, size);
    }

    // 특정 체험단 상세 조회
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<?> getCampaignById(@PathVariable Long campaignId) {
        CampaignRegistrationResponse response = adminService.getCampaignById(campaignId);
        return API.OK(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/campaigns/{campaignId}/approve")
    public ResponseEntity<CampaignApprovalResponse> approveCampaign(
            @PathVariable Long campaignId, @RequestBody CampaignApprovalRequest request) {
        CampaignApprovalResponse response = adminService.approveCampaign(campaignId, request);
        return API.OK(response);
    }
}
