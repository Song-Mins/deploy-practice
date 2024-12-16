package com.example.deploy.domain.campaign.model.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {
    VISIT("방문형"),
    PURCHASE("구매형"),
    DELIVERY("배송형"),
    PRESS_CORPS("기자단"),
    TAKEOUT("포장");

    private final String displayName;
}
