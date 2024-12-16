package com.example.deploy.domain.user.model.request;


import com.example.deploy.domain.user.model.entity.enums.Gender;
import com.example.deploy.global.validation.type.AllOrNone;
import java.time.LocalDate;

@AllOrNone(fields = {"address", "addressDetail", "postalCode"})
public record InfluencerExtraRegisterRequest(
        LocalDate birthday,
        Gender gender,
        String address,
        String addressDetail,
        String postalCode) {}
