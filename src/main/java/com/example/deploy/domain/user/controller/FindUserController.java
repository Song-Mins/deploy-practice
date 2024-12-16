package com.example.deploy.domain.user.controller;


import com.example.deploy.domain.user.model.request.FindUserIdRequest;
import com.example.deploy.domain.user.model.request.PasswordChangeRequest;
import com.example.deploy.domain.user.model.request.PasswordCheckRequest;
import com.example.deploy.domain.user.service.FindUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FindUserController {

    private final FindUserService findUserService;

    @PostMapping("/id/find")
    public ResponseEntity findUserId(@RequestBody FindUserIdRequest request) {
        return findUserService.findUserId(request);
    }

    @PostMapping("/password/change")
    public ResponseEntity passwordChange(@RequestBody PasswordChangeRequest request) {
        return findUserService.passwordChange(request);
    }

    @PostMapping("/password/change/check")
    public ResponseEntity passwordCheck(@RequestBody PasswordCheckRequest request) {
        return findUserService.passwordCheck(request);
    }
}
