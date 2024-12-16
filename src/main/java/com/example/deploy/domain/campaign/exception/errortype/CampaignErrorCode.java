package com.example.deploy.domain.campaign.exception.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CampaignErrorCode implements ErrorCode {
    CAMPAIGN_NOT_FOUND(HttpStatus.BAD_REQUEST, "체험단을 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "이미지를 등록해주세요."),
    CANNOT_MANAGE_CAMPAIGN(HttpStatus.BAD_REQUEST, "관리할수 없는 단계입니다."),
    CANNOT_DELETE_CAMPAIGN(HttpStatus.BAD_REQUEST, "해당 체험단은 삭제할 수 없습니다."),
    NO_CAMPAIGNS_FOUND(HttpStatus.NOT_FOUND, "선택한 조건에 맞는 체험단이 없습니다.");

    private final HttpStatus status;
    private final String msg;
}
