package com.example.deploy.domain.application.repository;


import com.example.deploy.domain.application.exception.ApplicationException;
import com.example.deploy.domain.application.exception.errortype.ApplicationErrorCode;
import com.example.deploy.domain.application.model.entity.Application;
import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import com.example.deploy.domain.campaign.model.entity.enums.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    default Application getApplicationById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.NOT_FOUND_BY_ID));
    }

    @Query(
            "SELECT a FROM Application a "
                    + "WHERE (:campaignState IS NULL OR a.campaign.campaignState = :campaignState) "
                    + "AND (:platform IS NULL OR a.campaign.platform = :platform) "
                    + "AND (:keyword IS NULL OR a.campaign.businessName LIKE %:keyword%) "
                    + "AND a.campaign.isDeleted = false "
                    + "AND a.user.id = :userId "
                    + "AND a.isDeleted = false")
    Page<Application> findByStateAndPlatformAndNameContainingAndUserId(
            @Param("campaignState") CampaignState campaignState,
            @Param("platform") Platform platform,
            @Param("keyword") String keyword,
            @Param("userId") Long userId,
            Pageable pageable);

    void deleteByIdAndUserId(Long id, Long userId);
}
