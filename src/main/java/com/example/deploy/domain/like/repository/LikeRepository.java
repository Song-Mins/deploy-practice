package com.example.deploy.domain.like.repository;


import com.example.deploy.domain.like.model.entity.Like;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndCampaignId(Long userId, Long campaignId);

    Page<Like> findByUserIdAndIsLiked(Long userId, boolean isLiked, Pageable pageable);

    @Query("SELECT l.campaign.id FROM Like l WHERE l.user.id = :userId AND l.isLiked = true")
    List<Long> findLikedCampaignIdsByUserId(@Param("userId") Long userId);
}
