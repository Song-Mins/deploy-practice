package com.example.deploy.domain.campaign.util;

public class AddressAndPointUtil {

    public static String[] extractCityAndDistrict(String address) { // (시/도), (구/군) 추출 메서드

        if (address == null || address.isEmpty()) {
            return new String[] {"", ""}; // address가 null이거나 빈 문자열일 경우 빈 값을 반환
        }

        String[] addressParts = address.split(" ");
        String rawCity = addressParts[0];

        String city = rawCity.replace("특별시", "").replace("광역시", "").replace("도", "");
        String district = addressParts[1];

        return new String[] {city, district};
    }

    public static int calculateTotalPoints(
            Integer capacity, Integer pointPerPerson) { // 총 포인트 계산 메서드
        if (capacity != null && pointPerPerson != null) {
            return (int) Math.round(capacity * pointPerPerson * 1.2);
        }
        return 0;
    }
}
