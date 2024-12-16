package com.example.deploy.domain.like.controller;


import com.example.deploy.domain.campaign.model.response.CampaignResponse;
import com.example.deploy.domain.like.service.LikeService;
import com.example.deploy.domain.user.config.model.CustomUserDetails;
import com.example.deploy.global.api.API;
import com.example.deploy.global.model.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("{campaignId}")
    public ResponseEntity<?> toggleLike(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long campaignId) {
        boolean isLiked = likeService.toggleLike(customUserDetails.getUserId(), campaignId);
        return API.OK(isLiked);
    }

    @GetMapping
    public ResponseEntity<?> getLikedCampaigns(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        PagedResponse<CampaignResponse> likedCampaigns =
                likeService.getLikedCampaigns(customUserDetails.getUserId(), pageable);
        return API.OK(likedCampaigns);
    }
}
