package com.example.deploy.domain.campaign.repository;


import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.model.request.CampaignSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampaignRepositoryCustom {
    Page<Campaign> searchCampaigns(
            CampaignSearchRequest searchRequest, Pageable pageable, Long userId);
}
