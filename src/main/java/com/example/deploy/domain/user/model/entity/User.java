package com.example.deploy.domain.user.model.entity;


import com.example.deploy.domain.application.model.entity.Application;
import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.post.model.entity.Post;
import com.example.deploy.domain.review.model.entity.Review;
import com.example.deploy.domain.user.exception.UserException;
import com.example.deploy.domain.user.exception.errortype.UserErrorCode;
import com.example.deploy.domain.user.model.entity.enums.Role;
import com.example.deploy.domain.user.model.request.EnterpriserChangeRequest;
import com.example.deploy.domain.user.model.request.EnterpriserExtraRegisterRequest;
import com.example.deploy.domain.user.model.request.InfluencerChangeRequest;
import com.example.deploy.domain.user.model.request.InfluencerExtraRegisterRequest;
import com.example.deploy.global.model.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    private String name;
    private String email;
    private String password;
    private String nickname;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Application> applicationList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Campaign> campaignList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Review> reviewList;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Influencer influencer;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Enterpriser enterpriser;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Post> posts;

    private Long point;
    private String phone;
    private String joinPath;
    private String address;
    private String addressDetail;
    private String postalCode;
    private String profileImage;
    private String profileImageUrl;
    private Boolean marketing;
    private Boolean penalty;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    public void delete() {

        // 사업주가 체험단을 등록했거나 인플루언서가 체험단을 신청 중일 때는 탈퇴가 불가능
        if (this.campaignList.size() != 0 || this.applicationList.size() != 0) {
            throw new UserException(UserErrorCode.FAILED_DELETE);
        }

        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void change(
            EnterpriserChangeRequest enterpriserChangeRequest, PasswordEncoder passwordEncoder) {
        // 비밀번호 일치하는지 검증
        if (!passwordEncoder.matches(enterpriserChangeRequest.oldPassword(), this.password)) {
            throw new UserException(UserErrorCode.FAILED_CHANGE);
        }
        this.password = passwordEncoder.encode(enterpriserChangeRequest.newPassword());
        this.name = enterpriserChangeRequest.name();
        this.nickname = enterpriserChangeRequest.nickname();
        this.phone = enterpriserChangeRequest.phone();
        this.address = enterpriserChangeRequest.address();
        this.addressDetail = enterpriserChangeRequest.addressDetail();
        this.postalCode = enterpriserChangeRequest.postalCode();
    }

    public void change(String password) {
        this.password = password;
    }

    // 완료
    public void change(
            EnterpriserExtraRegisterRequest enterpriserExtraRegisterRequest,
            String profileImage,
            String profileImageUrl) {
        this.profileImage = profileImage;
        this.profileImageUrl = profileImageUrl;
        this.address = enterpriserExtraRegisterRequest.address();
        this.addressDetail = enterpriserExtraRegisterRequest.addressDetail();
        this.postalCode = enterpriserExtraRegisterRequest.postalCode();
    }

    // 완료
    public void change(
            InfluencerExtraRegisterRequest influencerExtraRegisterRequest,
            String profileImage,
            String profileImageUrl) {
        this.profileImage = profileImage;
        this.profileImageUrl = profileImageUrl;
        this.address = influencerExtraRegisterRequest.address();
        this.addressDetail = influencerExtraRegisterRequest.addressDetail();
        this.postalCode = influencerExtraRegisterRequest.postalCode();
        this.influencer.change(influencerExtraRegisterRequest);
    }

    public void change(
            InfluencerChangeRequest influencerChangeRequest, PasswordEncoder passwordEncoder) {
        // 비밀번호 일치하는지 검증
        if (!passwordEncoder.matches(influencerChangeRequest.oldPassword(), this.password)) {
            throw new UserException(UserErrorCode.FAILED_CHANGE);
        }
        this.password = passwordEncoder.encode(influencerChangeRequest.newPassword());
        this.name = influencerChangeRequest.name();
        this.nickname = influencerChangeRequest.nickname();
        this.phone = influencerChangeRequest.phone();
        this.address = influencerChangeRequest.address();
        this.addressDetail = influencerChangeRequest.addressDetail();
        this.postalCode = influencerChangeRequest.postalCode();
        this.influencer.change(influencerChangeRequest);
    }

    public void change(String profileImage, String profileImageUrl) {
        this.profileImage = profileImage;
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isNotSame(Long userId) {
        return !this.getId().equals(userId);
    }

    public Boolean isInfluencer() {
        return this.influencer != null;
    }
}
