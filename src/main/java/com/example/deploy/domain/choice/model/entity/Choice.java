package com.example.deploy.domain.choice.model.entity;


import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.global.model.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Choice extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    public static Choice from(User user, Campaign campaign) {
        return Choice.builder().user(user).campaign(campaign).build();
    }
}
