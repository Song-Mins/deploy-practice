package com.example.deploy.domain.user.model.entity;


import com.example.deploy.domain.user.model.entity.enums.Gender;
import com.example.deploy.domain.user.model.request.InfluencerChangeRequest;
import com.example.deploy.domain.user.model.request.InfluencerExtraRegisterRequest;
import com.example.deploy.global.model.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Influencer extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "influencer", fetch = FetchType.LAZY)
    private List<Sns> snsList;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthday;

    // 완료
    public void change(InfluencerExtraRegisterRequest influencerExtraRegisterRequest) {
        this.gender = influencerExtraRegisterRequest.gender();
        this.birthday = influencerExtraRegisterRequest.birthday();
    }

    // 완료
    public void change(InfluencerChangeRequest influencerChangeRequest) {

        this.snsList =
                influencerChangeRequest.snsRequestList().stream()
                        .map(snsRequest -> snsRequest.toSns(this))
                        .toList();
        this.gender = influencerChangeRequest.gender();
        this.birthday = influencerChangeRequest.birthday();
    }
}
