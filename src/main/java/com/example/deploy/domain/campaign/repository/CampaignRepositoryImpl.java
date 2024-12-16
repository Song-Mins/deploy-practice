package com.example.deploy.domain.campaign.repository;


import com.example.deploy.domain.campaign.exception.CampaignException;
import com.example.deploy.domain.campaign.exception.errortype.CampaignErrorCode;
import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.model.entity.QCampaign;
import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import com.example.deploy.domain.campaign.model.entity.enums.SortBy;
import com.example.deploy.domain.campaign.model.request.CampaignSearchRequest;
import com.example.deploy.domain.like.repository.LikeRepository;
import com.example.deploy.domain.user.exception.UserException;
import com.example.deploy.domain.user.exception.errortype.UserErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final LikeRepository likeRepository;

    @Override
    public Page<Campaign> searchCampaigns(
            CampaignSearchRequest searchRequest, Pageable pageable, Long userId) {
        QCampaign campaign = QCampaign.campaign;

        BooleanBuilder builder = new BooleanBuilder();

        // 로그인한 사용자의 찜 목록 필터링
        if (searchRequest.likeFilter()) {
            // 비회원인 경우 예외 발생
            if (userId == null) {
                throw new UserException(UserErrorCode.LOGIN_REQUIRED);
            }
            // 모든 필터 해제 후 찜한 캠페인만 표시
            List<Long> likedCampaignIds = likeRepository.findLikedCampaignIdsByUserId(userId);
            if (likedCampaignIds.isEmpty()) {
                builder.and(campaign.id.in(0L)); // 결과가 없도록 설정
            } else {
                builder.and(campaign.id.in(likedCampaignIds));
            }

        } else {
            // 일반 검색 조건 필터링 로직
            if (searchRequest.category() != null) {
                builder.and(campaign.category.eq(searchRequest.category()));
            }

            if (searchRequest.platform() != null) {
                builder.and(campaign.platform.eq(searchRequest.platform()));
            }

            if (searchRequest.type() != null) {
                builder.and(campaign.type.eq(searchRequest.type()));
            }

            if (searchRequest.campaignState() != null) {
                builder.and(campaign.campaignState.eq(searchRequest.campaignState()));
            }

            if (searchRequest.keyword() != null) {
                builder.and(campaign.businessName.containsIgnoreCase(searchRequest.keyword()));
            }

            if (searchRequest.cities() != null && !searchRequest.cities().isEmpty()) {
                builder.and(campaign.city.in(searchRequest.cities()));
            }

            if (searchRequest.districts() != null && !searchRequest.districts().isEmpty()) {
                builder.and(campaign.district.in(searchRequest.districts()));
            }
        }

        // 체험단 상태가 모집중인 체험단만 필터링
        builder.and(campaign.campaignState.eq(CampaignState.RECRUITING));
        // 삭제 상태가 아닌 체험단만 필터링
        builder.and(campaign.isDeleted.isFalse());

        // 쿼리 생성
        var query =
                queryFactory
                        .selectFrom(campaign)
                        .where(builder)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize());

        if (searchRequest.sortBy() == SortBy.POPULAR) { // 인기순
            query.orderBy(campaign.currentApplicants.desc(), campaign.labelOrderingNumber.asc());
        } else if (searchRequest.sortBy() == SortBy.CLOSING_SOON) { // 마감임박순
            query.orderBy(
                    campaign.applicationEndDate.asc(),
                    campaign.approvedDate.asc(),
                    campaign.labelOrderingNumber.asc());
        } else if (searchRequest.sortBy() == SortBy.NEWEST) { // 최신순
            query.orderBy(campaign.id.desc(), campaign.labelOrderingNumber.asc());
        } else { // 기본 정렬 값은 추천순
            query.orderBy(
                    campaign.pointPerPerson.desc(),
                    campaign.currentApplicants.asc(),
                    campaign.approvedDate.desc(),
                    campaign.labelOrderingNumber.asc());
        }

        List<Campaign> results = query.fetch();
        long total = queryFactory.selectFrom(campaign).where(builder).fetchCount();

        // 정렬 조건에 맞는 체험단이 없을 경우 예외 발생
        if (results.isEmpty()) {
            throw new CampaignException(CampaignErrorCode.NO_CAMPAIGNS_FOUND);
        }

        return new PageImpl<>(results, pageable, total);
    }
}
