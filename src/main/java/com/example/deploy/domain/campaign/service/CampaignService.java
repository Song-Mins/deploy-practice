package com.example.deploy.domain.campaign.service;


import com.example.deploy.domain.application.model.response.ApplicantResponse;
import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.model.entity.LabelOrdering;
import com.example.deploy.domain.campaign.model.request.CampaignFilterRequest;
import com.example.deploy.domain.campaign.model.request.CampaignRequest;
import com.example.deploy.domain.campaign.model.request.CampaignSearchRequest;
import com.example.deploy.domain.campaign.model.response.CampaignHomeResponse;
import com.example.deploy.domain.campaign.model.response.CampaignRegistrationResponse;
import com.example.deploy.domain.campaign.model.response.CampaignResponse;
import com.example.deploy.domain.campaign.model.response.CampaignSummaryResponse;
import com.example.deploy.domain.campaign.repository.CampaignRepository;
import com.example.deploy.domain.campaign.repository.LabelOrderingRepository;
import com.example.deploy.domain.choice.model.response.ChoiceInfluencerResponse;
import com.example.deploy.domain.review.model.response.ReviewerResponse;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.model.response.PagedResponse;
import com.example.deploy.global.type.S3PathPrefixType;
import com.example.deploy.global.util.S3Util;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final LabelOrderingRepository labelOrderingRepository;
    private final S3Util s3Util;

    @Transactional
    public CampaignRegistrationResponse createCampaign(
            Long userId, CampaignRequest campaignRequest, MultipartFile imageFile) {

        User user = userRepository.getUserById(userId);

        String imageFileName =
                s3Util.saveImage(imageFile, S3PathPrefixType.S3_CAMPAIGN_THUMBNAIL_PATH.toString());
        String imageUrl =
                s3Util.selectImage(
                        imageFileName, S3PathPrefixType.S3_CAMPAIGN_THUMBNAIL_PATH.toString());

        Campaign campaign = Campaign.create(user, imageFileName, imageUrl, campaignRequest);

        LabelOrdering labelOrdering =
                labelOrderingRepository.getLabelOrderingByLabel(
                        campaign.getLabel().getDisplayName());

        campaign.setLabelOrderingNumber(labelOrdering.getOrdering());
        campaignRepository.save(campaign);

        return CampaignRegistrationResponse.from(campaign);
    }

    @Transactional(readOnly = true)
    public CampaignResponse getCampaignById(Long campaignId, Long userId) {
        Campaign campaign = campaignRepository.getCampaignById(campaignId);
        return CampaignResponse.from(campaign, userId);
    }

    public void deleteCampaign(Long userId, Long campaignId) { // 체험단 삭제(취소)
        User user = userRepository.getUserById(userId);
        Campaign campaign = campaignRepository.getCampaignById(campaignId);
        campaign.delete(user);
    }

    // 사업주가 등록한 체험단 목록 조회
    @Transactional(readOnly = true)
    public Page<CampaignSummaryResponse> getRegisteredCampaigns(
            CampaignFilterRequest campaignFilterRequest, Pageable pageable, Long userId) {

        Page<Campaign> campaignPage =
                campaignRepository.findByStateAndPlatformAndNameContainingAndUserId(
                        campaignFilterRequest.campaignState(),
                        campaignFilterRequest.platform(),
                        campaignFilterRequest.keyword(),
                        userId,
                        pageable);

        return CampaignSummaryResponse.from(campaignPage, userId);
    }

    // 체험단 검색
    @Transactional(readOnly = true)
    public PagedResponse<CampaignSummaryResponse> searchCampaigns(
            CampaignSearchRequest searchRequest, Pageable pageable, Long userId) {

        Page<Campaign> campaignPage =
                campaignRepository.searchCampaigns(searchRequest, pageable, userId);
        List<CampaignSummaryResponse> content =
                CampaignSummaryResponse.from(campaignPage, userId).getContent();

        return PagedResponse.of(
                content, campaignPage.getTotalElements(), campaignPage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public List<ApplicantResponse> getApplicants(Long campaignId, Long userId) {
        Campaign campaign = campaignRepository.getCampaignById(campaignId);
        return ApplicantResponse.of(campaign, userId);
    }

    @Transactional(readOnly = true)
    public List<ChoiceInfluencerResponse> getSelectedInfluencers(Long campaignId, Long userId) {
        Campaign campaign = campaignRepository.getCampaignById(campaignId);
        return ChoiceInfluencerResponse.of(campaign, userId);
    }

    @Transactional(readOnly = true)
    public List<ReviewerResponse> getReviews(Long campaignId, Long userId) {
        Campaign campaign = campaignRepository.getCampaignById(campaignId);
        return ReviewerResponse.of(campaign, userId);
    }

    @Transactional(readOnly = true)
    public CampaignHomeResponse getCampaignForHomeScreen(Long userId) {
        List<Campaign> premium = campaignRepository.findPremiumCampaigns();
        List<Campaign> popular = campaignRepository.findPopularCampaigns();
        List<Campaign> newest = campaignRepository.findNewestCampaigns();
        List<Campaign> imminent = campaignRepository.findImminentDueDateCampaigns();

        return CampaignHomeResponse.of(
                CampaignSummaryResponse.from(premium, userId),
                CampaignSummaryResponse.from(popular, userId),
                CampaignSummaryResponse.from(newest, userId),
                CampaignSummaryResponse.from(imminent, userId));
    }
}
