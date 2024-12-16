package com.example.deploy.domain.user.model.request;



import com.example.deploy.domain.user.model.entity.enums.Gender;
import com.example.deploy.global.validation.type.AllOrNone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@AllOrNone(fields = {"address", "addressDetail", "postalCode"})
@AllOrNone(fields = {"oldPassword", "newPassword"})
public record InfluencerChangeRequest(
        String oldPassword,
        String newPassword,
        @NotBlank String name,
        @NotBlank String nickname,
        @NotBlank String phone,
        @Size(min = 1, max = 5) List<SnsRequest> snsRequestList,
        String address,
        String addressDetail,
        String postalCode,
        LocalDate birthday,
        Gender gender) {}
