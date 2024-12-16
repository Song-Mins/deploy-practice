package com.example.deploy.domain.user.service;


import com.example.deploy.domain.user.exception.RegisterException;
import com.example.deploy.domain.user.exception.errortype.RegisterErrorCode;
import com.example.deploy.domain.user.model.request.EmailCheckRequest;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.api.API;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ResponseEntity emailCheck(EmailCheckRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent())
            throw new RegisterException(RegisterErrorCode.EMAIL_SAME);
        return API.OK();
    }
}
