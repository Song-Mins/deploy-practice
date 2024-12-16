package com.example.deploy.domain.campaign.model.request;


import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import com.example.deploy.domain.campaign.model.entity.enums.Category;
import com.example.deploy.domain.campaign.model.entity.enums.Platform;
import com.example.deploy.domain.campaign.model.entity.enums.SortBy;
import com.example.deploy.domain.campaign.model.entity.enums.Type;
import java.util.List;

public record CampaignSearchRequest(
    List<String> cities, // 시/도
    List<String> districts, // 구/군
    Category category, // 카테고리
    Platform platform, // 플랫폼
    Type type, // 체험단 유형
    CampaignState campaignState, // 상태 (모집중, 검수중 등)
    Boolean likeFilter, // 좋아요 필터 유무
    String keyword, // 검색 키워드
    SortBy sortBy // 정렬 방식: 추천순, 인기순, 마감임박순, 최신순
        ) {
    // likedFilter가 null인 경우 false로 처리
    public Boolean likeFilter() {
        return likeFilter != null && likeFilter;
    }
}
