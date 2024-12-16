package com.example.deploy.domain.user.controller;


import com.example.deploy.domain.user.model.request.EmailCheckRequest;
import com.example.deploy.domain.user.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sign-up")
public class SignUpController {

    private final SignUpService service;

    @PostMapping("/email/check")
    public ResponseEntity emailCheck(@RequestBody EmailCheckRequest request) {
        return service.emailCheck(request);
    }
}
