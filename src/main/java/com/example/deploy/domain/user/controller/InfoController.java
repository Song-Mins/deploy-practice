package com.example.deploy.domain.user.controller;


import com.example.deploy.domain.user.model.request.ImpInfoRequest;
import com.example.deploy.domain.user.model.request.OAuthInfoRequest;
import com.example.deploy.domain.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/info")
public class InfoController {

    private final InfoService infoService;

    @PostMapping("/oauth")
    public ResponseEntity getOAuthInfo(@RequestBody OAuthInfoRequest request) {
        return infoService.getOAuthInfo(request);
    }

    @PostMapping("/imp")
    public ResponseEntity getImpInfo(@RequestBody ImpInfoRequest impInfoRequest) {
        return infoService.getImpInfo(impInfoRequest);
    }
}
