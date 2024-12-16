package com.example.deploy.domain.like.model.entity;



import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.global.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "likes")
public class Like extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(name = "is_liked", nullable = false)
    private boolean isLiked;

    // Like 생성 메서드
    public static Like create(User user, Campaign campaign) {
        return Like.builder()
                .user(user)
                .campaign(campaign)
                .isLiked(true) // 처음 생성시 무조건 좋아요 상태로 생성
                .build();
    }

    // 좋아요 상태 토글 메서드
    public Like withToggleStatus() {
        return Like.builder()
                .id(this.getId())
                .user(this.getUser())
                .campaign(this.getCampaign())
                .isLiked(!this.isLiked) // 현재 상태의 반대로 설정
                .build();
    }
}
