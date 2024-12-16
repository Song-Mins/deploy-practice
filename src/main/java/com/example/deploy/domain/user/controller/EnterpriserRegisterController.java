package com.example.deploy.domain.user.controller;


import com.example.deploy.domain.user.model.request.EnterpriserOAuthSignUpRequest;
import com.example.deploy.domain.user.model.request.EnterpriserSignUpRequest;
import com.example.deploy.domain.user.service.EnterpriserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enterpriser/sign-up")
public class EnterpriserRegisterController {

    private final EnterpriserService enterpriserService;

    @PostMapping
    public ResponseEntity singUpEnterpriser(@RequestBody EnterpriserSignUpRequest request) {
        return enterpriserService.signUpEnterpriser(request);
    }

    @PostMapping("/oauth")
    public ResponseEntity singUpOAuthEnterpriser(
            @RequestBody EnterpriserOAuthSignUpRequest request) {
        return enterpriserService.singUpOAuthEnterpriser(request);
    }
}
