package com.example.deploy.domain.user.model.response;


import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.model.entity.enums.Gender;
import java.time.LocalDate;
import java.util.List;

public record InfluencerChangeResponse(
        String name,
        List<SnsResponse> snsResponseList,
        Integer likeCnt,
        String email,
        String phone,
        String nickname,
        String address,
        String addressDetail,
        String postalCode,
        LocalDate birthday,
        Gender gender) {

    public static InfluencerChangeResponse from(User user) {

        // 사용자가 좋아요한 캠페인 수 구하기
        int likeCnt = user.getCampaignList().size();

        // List<Sns> -> List<SnsResponse>
        List<SnsResponse> snsResponseList =
                user.getInfluencer().getSnsList().stream().map(SnsResponse::from).toList();

        return new InfluencerChangeResponse(
                user.getName(),
                snsResponseList,
                likeCnt,
                user.getEmail(),
                user.getPhone(),
                user.getNickname(),
                user.getAddress(),
                user.getAddressDetail(),
                user.getPostalCode(),
                user.getInfluencer().getBirthday(),
                user.getInfluencer().getGender());
    }
}
