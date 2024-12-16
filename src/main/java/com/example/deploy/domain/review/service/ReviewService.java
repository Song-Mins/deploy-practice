package com.example.deploy.domain.review.service;

import com.example.deploy.domain.Image.entity.enums.ContentType;
import com.example.deploy.domain.Image.service.ImageFileService;
import com.example.deploy.domain.application.model.entity.Application;
import com.example.deploy.domain.application.repository.ApplicationRepository;
import com.example.deploy.domain.review.model.entity.Review;
import com.example.deploy.domain.review.model.request.ReviewRequest;
import com.example.deploy.domain.review.repository.ReviewRepository;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.type.S3PathPrefixType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ImageFileService imageFileService;

    @Transactional
    public void createReview(
            Long userId,
            Long applicationId,
            ReviewRequest reviewRequest,
            List<MultipartFile> imageFiles) {
        User user = userRepository.getUserById(userId);
        Application application = applicationRepository.getApplicationById(applicationId);

        application.checkReviewCreateAble(userId);
        Review review =
                Review.builder()
                        .user(user)
                        .campaign(application.getCampaign())
                        .application(application)
                        .url(reviewRequest.url())
                        .build();
        reviewRepository.save(review);

        imageFileService.saveImageFiles(
                imageFiles,
                ContentType.REVIEW,
                review.getId(),
                S3PathPrefixType.S3_REVIEW_IMAGE_PATH);
    }
}
