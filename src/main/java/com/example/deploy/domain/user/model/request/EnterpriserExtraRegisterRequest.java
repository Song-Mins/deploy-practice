package com.example.deploy.domain.user.model.request;


import com.example.deploy.global.validation.type.AllOrNone;

@AllOrNone(fields = {"address", "addressDetail", "postalCode"})
public record EnterpriserExtraRegisterRequest(
        String address, String addressDetail, String postalCode) {}
