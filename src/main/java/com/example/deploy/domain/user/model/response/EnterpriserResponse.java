package com.example.deploy.domain.user.model.response;


import com.example.deploy.domain.user.model.entity.User;

public record EnterpriserResponse(String profileImageUrl, String nickname) {

    // 완성
    public static EnterpriserResponse from(User user) {
        return new EnterpriserResponse(user.getProfileImageUrl(), user.getNickname());
    }
}
