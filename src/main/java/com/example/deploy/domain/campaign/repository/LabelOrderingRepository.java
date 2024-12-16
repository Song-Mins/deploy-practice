package com.example.deploy.domain.campaign.repository;


import com.example.deploy.domain.campaign.exception.CampaignException;
import com.example.deploy.domain.campaign.exception.errortype.CampaignErrorCode;
import com.example.deploy.domain.campaign.model.entity.LabelOrdering;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelOrderingRepository extends JpaRepository<LabelOrdering, Long> {
    Optional<LabelOrdering> findByLabel(String label);

    // 기본 메서드 추가
    default LabelOrdering getLabelOrderingByLabel(String label) {
        return findByLabel(label)
                .orElseThrow(() -> new CampaignException(CampaignErrorCode.CANNOT_MANAGE_CAMPAIGN));
    }
}
