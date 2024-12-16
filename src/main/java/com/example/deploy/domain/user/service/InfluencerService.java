package com.example.deploy.domain.user.service;


import com.example.deploy.domain.Image.service.ImageFileService;
import com.example.deploy.domain.auth.client.GoogleApiClient;
import com.example.deploy.domain.auth.client.KakaoApiClient;
import com.example.deploy.domain.auth.client.NaverApiClient;
import com.example.deploy.domain.user.exception.RegisterException;
import com.example.deploy.domain.user.exception.errortype.RegisterErrorCode;
import com.example.deploy.domain.user.model.entity.Influencer;
import com.example.deploy.domain.user.model.entity.Sns;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.model.entity.enums.Gender;
import com.example.deploy.domain.user.model.entity.enums.Role;
import com.example.deploy.domain.user.model.request.InfluencerChangeRequest;
import com.example.deploy.domain.user.model.request.InfluencerExtraRegisterRequest;
import com.example.deploy.domain.user.model.request.InfluencerOAuthSignUpRequest;
import com.example.deploy.domain.user.model.request.InfluencerSignUpRequest;
import com.example.deploy.domain.user.model.request.SingUpSns;
import com.example.deploy.domain.user.model.response.InfluencerChangeResponse;
import com.example.deploy.domain.user.model.response.InfluencerResponse;
import com.example.deploy.domain.user.repository.InfluencerRepository;
import com.example.deploy.domain.user.repository.SnsRepository;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.api.API;
import com.example.deploy.global.type.S3PathPrefixType;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Certification;
import com.siot.IamportRestClient.response.IamportResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class InfluencerService {

    private final IamportClient iamportClient;
    private final UserRepository userRepository;
    private final InfluencerRepository influencerRepository;
    private final ImageFileService imageFileService;
    private final SnsRepository snsRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoApiClient kakaoApiClient;
    private final GoogleApiClient googleApiClient;
    private final NaverApiClient naverApiClient;

    @Transactional
    public void signUpExtra(
            Long id,
            InfluencerExtraRegisterRequest influencerExtraRegisterRequest,
            MultipartFile imageFile) {

        User user = userRepository.getUserById(id);
        String profileImage = null;
        String profileImageUrl = null;

        // 이미지 처리 로직을 ImageService로 위임
        if (imageFile != null) {
            profileImage =
                    imageFileService.validateAndUploadImage(
                            imageFile, S3PathPrefixType.S3_PROFILE_IMAGE_PATH);
            profileImageUrl =
                    imageFileService.selectImage(
                            profileImage, S3PathPrefixType.S3_PROFILE_IMAGE_PATH);
        }

        user.change(influencerExtraRegisterRequest, profileImage, profileImageUrl);
    }

    @Transactional
    public InfluencerResponse getMyPage(Long id) {

        User user = userRepository.getUserById(id);

        return InfluencerResponse.from(user);
    }

    // 인플루언서 회원정보 변경 페이지
    @Transactional
    public InfluencerChangeResponse getChangePage(Long id) {

        User user = userRepository.getUserById(id);
        return InfluencerChangeResponse.from(user);
    }

    // 인플루언서 회원정보 변경
    @Transactional
    public InfluencerChangeResponse changeInformation(
            InfluencerChangeRequest influencerChangeRequest, Long id) {

        User user = userRepository.getUserById(id);
        user.change(influencerChangeRequest, passwordEncoder);

        return InfluencerChangeResponse.from(user);
    }

    @Transactional
    public ResponseEntity signUpInfluencer(InfluencerSignUpRequest request) {
        try {
            IamportResponse<Certification> certification =
                    iamportClient.certificationByImpUid(request.impId());

            if (!certification.getResponse().getName().equals(request.name())) {
                throw new RegisterException(RegisterErrorCode.FAIL_IMP_NAME_NOT_SAME);
            }

            if (userRepository.findByEmail(request.email()).isPresent()) {
                throw new RegisterException(RegisterErrorCode.EMAIL_SAME);
            }

            User user =
                    userRepository.save(
                            User.builder()
                                    .email(request.email())
                                    .role(Role.ROLE_INFLUENCER)
                                    .marketing(request.marketing())
                                    .isDeleted(false)
                                    .phone(certification.getResponse().getPhone())
                                    .point(0L)
                                    .nickname(request.nickname())
                                    .name(request.name())
                                    .password(passwordEncoder.encode(request.password()))
                                    .joinPath(request.joinPath())
                                    .build());
            Influencer influencer =
                    influencerRepository.save(
                            Influencer.builder()
                                    .user(user)
                                    .gender(
                                            Gender.valueOf(
                                                    certification
                                                            .getResponse()
                                                            .getGender()
                                                            .toUpperCase()))
                                    .birthday(
                                            certification
                                                    .getResponse()
                                                    .getBirth()
                                                    .toInstant()
                                                    .atZone(ZoneId.systemDefault())
                                                    .toLocalDate())
                                    .build());

            for (SingUpSns snsType : request.snsResponseList()) {
                snsRepository.save(
                        Sns.builder()
                                .snsType(snsType.snsType())
                                .url(snsType.url())
                                .influencer(influencer)
                                .build());
            }

        } catch (IamportResponseException | IOException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        }

        return API.OK();
    }

    @Transactional
    public ResponseEntity singUpOAuthInfluencer(InfluencerOAuthSignUpRequest request) {
        try {
            IamportResponse<Certification> certification =
                    iamportClient.certificationByImpUid(request.impId());

            if (!certification.getResponse().getName().equals(request.name()))
                throw new RegisterException(RegisterErrorCode.FAIL_IMP_NAME_NOT_SAME);

            if (userRepository.findByEmail(request.email()).isPresent())
                throw new RegisterException(RegisterErrorCode.EMAIL_SAME);

            User user =
                    userRepository.save(
                            User.builder()
                                    .email(request.email())
                                    .role(Role.ROLE_INFLUENCER)
                                    .marketing(request.marketing())
                                    .isDeleted(false)
                                    .phone(certification.getResponse().getPhone())
                                    .point(0L)
                                    .nickname(request.nickname())
                                    .name(request.name())
                                    .password(passwordEncoder.encode("OAuth"))
                                    .joinPath(request.joinPath())
                                    .build());
            Influencer influencer =
                    influencerRepository.save(
                            Influencer.builder()
                                    .user(user)
                                    .gender(
                                            Gender.valueOf(
                                                    certification
                                                            .getResponse()
                                                            .getGender()
                                                            .toUpperCase()))
                                    .birthday(
                                            certification
                                                    .getResponse()
                                                    .getBirth()
                                                    .toInstant()
                                                    .atZone(ZoneId.systemDefault())
                                                    .toLocalDate())
                                    .build());

            for (SingUpSns snsType : request.snsResponseList()) {
                snsRepository.save(
                        Sns.builder()
                                .snsType(snsType.snsType())
                                .url(snsType.url())
                                .influencer(influencer)
                                .build());
            }

        } catch (IamportResponseException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        } catch (IOException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        }

        return API.OK();
    }
}
