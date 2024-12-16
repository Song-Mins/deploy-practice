package com.example.deploy.domain.admin.service;



import com.example.deploy.domain.admin.model.request.CampaignApprovalRequest;
import com.example.deploy.domain.admin.model.response.AdminCampaignListResponse;
import com.example.deploy.domain.admin.model.response.CampaignApprovalResponse;
import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import com.example.deploy.domain.campaign.model.response.CampaignRegistrationResponse;
import com.example.deploy.domain.campaign.repository.CampaignRepository;
import com.example.deploy.global.api.API;
import com.example.deploy.global.model.response.PagedResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CampaignRepository campaignRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<?> getUnapprovedCampaigns(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // 검수 중(INSPECTION)인 캠페인만 조회
        Page<Campaign> campaignPage = campaignRepository.findByCampaignStateInspection(pageable);

        // 조회한 캠페인을 응답 객체로 변환
        List<AdminCampaignListResponse> content =
                campaignPage.getContent().stream()
                        .map(AdminCampaignListResponse::from)
                        .collect(Collectors.toList());

        // 페이징 응답 생성
        PagedResponse<AdminCampaignListResponse> response =
                new PagedResponse<>(
                        content, campaignPage.getTotalElements(), campaignPage.getTotalPages());

        return API.OK(response);
    }

    // 특정 체험단 상세 조회 서비스 추가
    @Transactional(readOnly = true)
    public CampaignRegistrationResponse getCampaignById(Long campaignId) {
        Campaign campaign = campaignRepository.getCampaignById(campaignId);
        return CampaignRegistrationResponse.from(campaign);
    }

    @Transactional
    public CampaignApprovalResponse approveCampaign( // 체험단 승인
            Long campaignId, CampaignApprovalRequest request) {
        Campaign campaign = campaignRepository.getCampaignById(campaignId);

        campaign.setApplicationStartDate(request.applicationStartDate());
        campaign.setApplicationEndDate(request.applicationEndDate());
        campaign.setAnnouncementDate(request.announcementDate());
        campaign.setExperienceStartDate(request.experienceStartDate());
        campaign.setExperienceEndDate(request.experienceEndDate());
        campaign.setCampaignState(CampaignState.RECRUITING); // 승인 후 상태 변경

        // 현재 시각을 승인일로 설정
        campaign.setApprovedDate(LocalDateTime.now());

        campaignRepository.save(campaign);

        // Campaign 엔티티를 DTO로 변환하여 반환
        return CampaignApprovalResponse.from(campaign);
    }
}
