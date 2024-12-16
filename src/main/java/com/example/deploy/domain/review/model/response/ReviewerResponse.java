package com.example.deploy.domain.review.model.response;


import com.example.deploy.domain.campaign.exception.CampaignException;
import com.example.deploy.domain.campaign.exception.errortype.CampaignErrorCode;
import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import com.example.deploy.domain.review.model.entity.Review;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ReviewerResponse(
    String name, String phone, String message, LocalDate reviewDate, String reviewUrl) {

    public ReviewerResponse(String name, String phone, String message) {
        this(name, phone, message, null, null);
    }

    public static List<ReviewerResponse> of(Campaign campaign, Long userId) {
        // 캠페인의 유저아이디와 로그인 유저 아이디 비교
        if (!userId.equals(campaign.getUser().getId())) {
            throw new CampaignException(CampaignErrorCode.CANNOT_MANAGE_CAMPAIGN);
        }
        // 체험&리뷰, 리뷰마감 단계인지 확인
        if (!(CampaignState.EXPERIENCE_AND_REVIEW.equals(campaign.getCampaignState())
                || CampaignState.REVIEW_CLOSED.equals(campaign.getCampaignState()))) {
            throw new CampaignException(CampaignErrorCode.CANNOT_MANAGE_CAMPAIGN);
        }

        Map<Long, Review> reviewMap =
                campaign.getReviewList().stream()
                        .collect(
                                Collectors.toMap(
                                        review -> review.getUser().getId(), // 키는 User의 ID
                                        review -> review // 값은 Review 객체
                                        ));

        return campaign.getApplicationList().stream()
                .map(
                        application -> {
                            Long applicationUserId = application.getUser().getId();

                            if (reviewMap.containsKey(applicationUserId)) {
                                Review review = reviewMap.get(applicationUserId);
                                return new ReviewerResponse(
                                        application.getUser().getName(),
                                        application.getUser().getPhone(),
                                        application.getMessage(),
                                        review.getCreatedAt().toLocalDate(),
                                        review.getUrl());
                            } else {
                                return new ReviewerResponse(
                                        application.getUser().getName(),
                                        application.getUser().getPhone(),
                                        application.getMessage());
                            }
                        })
                .toList();
    }
}
