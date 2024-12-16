package com.example.deploy.domain.user.service;


import com.example.deploy.domain.user.exception.RegisterException;
import com.example.deploy.domain.user.exception.errortype.RegisterErrorCode;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.model.request.FindUserIdRequest;
import com.example.deploy.domain.user.model.request.PasswordChangeRequest;
import com.example.deploy.domain.user.model.request.PasswordCheckRequest;
import com.example.deploy.domain.user.model.response.FindUserIdResponse;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.api.API;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Certification;
import com.siot.IamportRestClient.response.IamportResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindUserService {

    private final IamportClient iamportClient;
    private final UserRepository userRepository;
    private final PasswordEncoder pe;

    @Transactional(readOnly = true)
    public ResponseEntity findUserId(FindUserIdRequest request) {
        try {
            IamportResponse<Certification> certification =
                    iamportClient.certificationByImpUid(request.impId());

            if (certification.getResponse().getName().equals(request.name()))
                throw new RegisterException(RegisterErrorCode.FAIL_IMP_NAME_NOT_SAME);

            User user =
                    userRepository
                            .findByNameAndPhone(
                                    request.name(), certification.getResponse().getPhone())
                            .orElseThrow(
                                    () -> new RegisterException(RegisterErrorCode.NOT_FOUND_USER));

            return API.OK(new FindUserIdResponse(user.getEmail()));
        } catch (IamportResponseException | IOException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        }
    }

    @Transactional
    public ResponseEntity passwordChange(PasswordChangeRequest request) {
        try {
            IamportResponse<Certification> certification =
                    iamportClient.certificationByImpUid(request.impId());

            if (!certification.getResponse().getName().equals(request.name()))
                throw new RegisterException(RegisterErrorCode.FAIL_IMP_NAME_NOT_SAME);

            User user =
                    userRepository
                            .findByNameAndPhone(
                                    request.name(), certification.getResponse().getPhone())
                            .orElseThrow(
                                    () -> new RegisterException(RegisterErrorCode.NOT_FOUND_USER));

            user.change(pe.encode(request.password()));

            return API.OK();
        } catch (IamportResponseException | IOException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        }
    }

    @Transactional
    public ResponseEntity passwordCheck(PasswordCheckRequest request) {
        try {
            IamportResponse<Certification> certification =
                    iamportClient.certificationByImpUid(request.impId());

            if (!certification.getResponse().getName().equals(request.name()))
                throw new RegisterException(RegisterErrorCode.FAIL_IMP_NAME_NOT_SAME);

            userRepository
                    .findByNameAndPhone(request.name(), certification.getResponse().getPhone())
                    .orElseThrow(() -> new RegisterException(RegisterErrorCode.NOT_FOUND_USER));

            return API.OK();
        } catch (IamportResponseException | IOException e) {
            throw new RegisterException(RegisterErrorCode.FAIL_IMP_ID);
        }
    }
}
