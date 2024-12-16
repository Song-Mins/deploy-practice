package com.example.deploy.domain.user.service;



import com.example.deploy.domain.Image.service.ImageFileService;
import com.example.deploy.domain.auth.client.GoogleApiClient;
import com.example.deploy.domain.auth.client.KakaoApiClient;
import com.example.deploy.domain.auth.client.NaverApiClient;
import com.example.deploy.domain.user.exception.RegisterException;
import com.example.deploy.domain.user.exception.errortype.RegisterErrorCode;
import com.example.deploy.domain.user.model.entity.Enterpriser;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.model.entity.enums.Role;
import com.example.deploy.domain.user.model.request.EnterpriserChangeRequest;
import com.example.deploy.domain.user.model.request.EnterpriserExtraRegisterRequest;
import com.example.deploy.domain.user.model.request.EnterpriserOAuthSignUpRequest;
import com.example.deploy.domain.user.model.request.EnterpriserSignUpRequest;
import com.example.deploy.domain.user.model.response.EnterpriserChangeResponse;
import com.example.deploy.domain.user.model.response.EnterpriserResponse;
import com.example.deploy.domain.user.repository.EnterpriserRepository;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.api.API;
import com.example.deploy.global.type.S3PathPrefixType;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Certification;
import com.siot.IamportRestClient.response.IamportResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EnterpriserService {

    private final UserRepository userRepository;
    private final EnterpriserRepository enterpriserRepository;
    private final ImageFileService imageFileService;
    private final IamportClient iamportClient;
    private final PasswordEncoder passwordEncoder;
    private final KakaoApiClient kakaoApiClient;
    private final GoogleApiClient googleApiClient;
    private final NaverApiClient naverApiClient;

    @Transactional
    public void signUpExtra(
            Long id,
            EnterpriserExtraRegisterRequest enterpriserExtraRegisterRequest,
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

        user.change(enterpriserExtraRegisterRequest, profileImage, profileImageUrl);
    }

    public EnterpriserResponse getMyPage(Long id) {

        User user = userRepository.getUserById(id);
        return EnterpriserResponse.from(user);
    }

    public EnterpriserChangeResponse getChangePage(Long id) {

        User user = userRepository.getUserById(id);
        return EnterpriserChangeResponse.from(user);
    }

    @Transactional
    public EnterpriserChangeResponse changeInformation(
            EnterpriserChangeRequest enterpriserChangeRequest, Long id) {

        User user = userRepository.getUserById(id);
        user.change(enterpriserChangeRequest, passwordEncoder);

        return EnterpriserChangeResponse.from(user);
    }

    @Transactional
    public ResponseEntity signUpEnterpriser(EnterpriserSignUpRequest request) {
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
                                    .role(Role.ROLE_ENTERPRISER)
                                    .marketing(request.marketing())
                                    .isDeleted(false)
                                    .phone(certification.getResponse().getPhone())
                                    .point(0L)
                                    .nickname(request.nickname())
                                    .name(request.name())
                                    .password(passwordEncoder.encode(request.password()))
                                    .joinPath(request.joinPath())
                                    .build());

            enterpriserRepository.save(
                    Enterpriser.builder().company(request.company()).user(user).build());

        } catch (IamportResponseException | IOException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        }
        return API.OK();
    }

    @Transactional
    public ResponseEntity singUpOAuthEnterpriser(EnterpriserOAuthSignUpRequest request) {
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

            enterpriserRepository.save(
                    Enterpriser.builder().company(request.company()).user(user).build());

        } catch (IamportResponseException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        } catch (IOException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        }
        return API.OK();
    }
}
