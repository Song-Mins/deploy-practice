package com.example.deploy.domain.user.model.response;


import com.example.deploy.domain.user.model.entity.User;

public record EnterpriserChangeResponse(
        String email,
        String name,
        String phone,
        String address,
        String addressDetail,
        String postalCode) {

    public static EnterpriserChangeResponse from(User user) {

        return new EnterpriserChangeResponse(
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getAddress(),
                user.getAddressDetail(),
                user.getPostalCode());
    }
}
