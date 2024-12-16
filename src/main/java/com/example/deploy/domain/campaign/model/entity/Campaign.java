package com.example.deploy.domain.campaign.model.entity;


import com.example.deploy.domain.application.model.entity.Application;
import com.example.deploy.domain.campaign.exception.CampaignException;
import com.example.deploy.domain.campaign.exception.errortype.CampaignErrorCode;
import com.example.deploy.domain.campaign.model.entity.enums.CampaignState;
import com.example.deploy.domain.campaign.model.entity.enums.Category;
import com.example.deploy.domain.campaign.model.entity.enums.Label;
import com.example.deploy.domain.campaign.model.entity.enums.Platform;
import com.example.deploy.domain.campaign.model.entity.enums.Type;
import com.example.deploy.domain.campaign.model.request.CampaignRequest;
import com.example.deploy.domain.campaign.util.AddressAndPointUtil;
import com.example.deploy.domain.choice.model.entity.Choice;
import com.example.deploy.domain.like.model.entity.Like;
import com.example.deploy.domain.review.model.entity.Review;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.global.model.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        name = "campaign",
        indexes = {
            @Index(name = "idx_is_deleted", columnList = "isDeleted"),
            @Index(name = "idx_campaign_state", columnList = "campaignState")
        })
public class Campaign extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 체험단을 등록한 사용자

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<Application> applicationList;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<Choice> choiceList;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<Review> reviewList;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<Like> likeList;

    @Enumerated(EnumType.STRING)
    private Platform platform; // 광고를 원하는 플랫폼 (예: 블로그, 인스타그램)

    @Enumerated(EnumType.STRING)
    private Type type; // 광고를 원하는 유형 (예: 방문형, 구매형)

    @Enumerated(EnumType.STRING)
    private Category category; // 광고를 원하는 카테고리 (예: 음식, 뷰티)

    private String serviceProvided; // 제공 내역

    private String businessName; // 상호명

    private String imageFileName; // 이미지 파일 명

    private String imageUrl; // 이미지 등록 URL

    private String contactNumber; // 연락처

    @Setter private Integer labelOrderingNumber; // 라벨 정렬 순서

    @Setter
    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<AvailableDay> availableDays; // 체험 가능 요일

    @Setter
    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Keyword> keywords; // 홍보용 키워드(태그)

    private Boolean pointPayment; // 포인트 지급 여부 (예/아니오)

    private Integer capacity; // 총 모집 인원 수

    private Integer currentApplicants; // 현재까지 신청 인원 수

    private Integer pointPerPerson; // 1인당 지급 포인트

    private Integer totalPoints; // 총 지급 포인트 (총 모집 인원 * 1인당 지급 포인트 * 수수료 20%)

    private String address; // 방문 체험할 장소의 주소

    private String addressDetail; // 상세 주소

    private String serviceUrl; // 구매형일 경우 서비스 URL

    private Integer postalCode; // 우편번호

    private String city; // 시/도

    private String district; // 구/군

    @Setter
    @Enumerated(EnumType.STRING)
    private CampaignState campaignState; // 체험단 상태 (검수중, 모집중 등)

    @Enumerated(EnumType.STRING)
    private Label label; // 라벨 (예: 다인체험단, 프리미엄, 일반체험단)

    private String requirement; // 사업주 요청 사항

    @Setter private LocalDateTime applicationStartDate; // 모집 시작일
    @Setter private LocalDateTime applicationEndDate; // 모집 종료일
    @Setter private LocalDateTime announcementDate; // 선정자 발표일
    @Setter private LocalDateTime experienceStartDate; // 체험 시작일
    @Setter private LocalDateTime experienceEndDate; // 체험 종료일

    private LocalTime experienceStartTime; // 체험 시작 시간

    private LocalTime experienceEndTime; // 체험 종료 시간

    @Setter private LocalDateTime reviewDate; // 리뷰 마감일

    @Setter private LocalDateTime approvedDate; // 체험단 승인일

    private Boolean isDeleted; // 삭제 여부

    public static Campaign create(
            User user, String imageFIleName, String imageUrl, CampaignRequest request) {
        Campaign campaign = new Campaign();
        campaign.user = user;
        campaign.imageFileName = imageFIleName;
        campaign.imageUrl = imageUrl;
        campaign.serviceUrl = request.serviceUrl();
        campaign.businessName = request.businessName();
        campaign.contactNumber = request.contactNumber();
        campaign.postalCode = request.postalCode();
        campaign.setAddress(request.address());
        campaign.addressDetail = request.addressDetail();
        campaign.platform = request.platform();
        campaign.type = request.type();
        campaign.category = request.category();
        campaign.pointPayment = request.pointPayment();
        campaign.capacity = request.capacity();
        campaign.pointPerPerson = request.pointPerPerson();
        campaign.serviceProvided = request.serviceProvided();
        campaign.requirement = request.requirement();
        campaign.experienceStartTime = request.experienceStartTime();
        campaign.experienceEndTime = request.experienceEndTime();
        campaign.campaignState = CampaignState.INSPECTION;
        campaign.isDeleted = false;

        campaign.label =
                Boolean.TRUE.equals(request.pointPayment())
                        ? Label.PREMIUM
                        : Label.GENERAL_CAMPAIGN;

        Set<AvailableDay> availableDays =
                (request.availableDays() != null ? request.availableDays() : Collections.emptySet())
                        .stream()
                                .map(day -> new AvailableDay(campaign, day.toString()))
                                .collect(Collectors.toSet());
        campaign.setAvailableDays(availableDays);

        Set<Keyword> keywords =
                request.keywords().stream()
                        .map(keyword -> new Keyword(campaign, keyword))
                        .collect(Collectors.toSet());
        campaign.setKeywords(keywords);

        campaign.calculateAndSetTotalPoints();
        return campaign;
    }

    public void calculateAndSetTotalPoints() {
        this.totalPoints =
                AddressAndPointUtil.calculateTotalPoints(this.capacity, this.pointPerPerson);
    }

    public boolean isCancelable() {
        return this.campaignState == CampaignState.RECRUITING
                || this.campaignState == CampaignState.RECRUITMENT_COMPLETED
                || this.campaignState == CampaignState.EXPERIENCE_AND_REVIEW;
    }

    // 캠페인 삭제 메서드 (취소 가능 여부와 소유자 검증 포함)
    public void delete(User user) {
        // 소유자 검증
        if (!this.user.getId().equals(user.getId())) {
            throw new CampaignException(CampaignErrorCode.UNAUTHORIZED_ACCESS);
        }
        // 취소 가능 여부 확인
        if (!isCancelable()) {
            throw new CampaignException(CampaignErrorCode.CANNOT_DELETE_CAMPAIGN);
        }
        // 캠페인 삭제 처리
        this.isDeleted = true;
    }

    public void setAddress(String address) {
        this.address = address;
        String[] cityAndDistrict = AddressAndPointUtil.extractCityAndDistrict(address);
        this.city = cityAndDistrict[0];
        this.district = cityAndDistrict[1];
    }

    public void change(User user) {
        this.user = user;
    }

    public boolean isNotReviewPeriod() {
        return LocalDateTime.now().isAfter(experienceEndDate)
                || LocalDateTime.now().isBefore(experienceStartDate);
    }

    public void addApplicantCount() {
        ++this.currentApplicants;
    }

    public void subtractApplicantCount() {
        --this.currentApplicants;
    }

    public Long calculateApplicationDeadline() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, this.applicationEndDate);
        return Math.max(duration.toDays(), 0);
    }

    public Boolean isLike(Long userId) {
        if (userId == null) {
            return false;
        }
        return this.likeList.stream()
                .anyMatch(like -> like.getUser().getId().equals(userId) && like.isLiked());
    }
}
