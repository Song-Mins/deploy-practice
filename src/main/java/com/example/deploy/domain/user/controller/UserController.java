package com.example.deploy.domain.user.controller;


import com.example.deploy.domain.user.config.model.CustomUserDetails;
import com.example.deploy.domain.user.model.response.UserHeaderResponse;
import com.example.deploy.domain.user.service.UserService;
import com.example.deploy.global.api.API;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 헤더 프로필 정보
    @GetMapping("header-info")
    public ResponseEntity<?> getHeaderInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        UserHeaderResponse userHeaderResponse =
                userService.getHeaderInfo(customUserDetails.getUserId());
        return API.OK(userHeaderResponse);
    }

    // 프로필 사진 수정
    @PatchMapping("/profile")
    public ResponseEntity update(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart(value = "imageFile") MultipartFile imageFile) {

        userService.update(customUserDetails.getUserId(), imageFile);

        return API.OK();
    }

    // 회원 탈퇴
    @DeleteMapping("")
    public ResponseEntity delete(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        userService.delete(customUserDetails.getUserId());

        return API.OK();
    }
}
