package com.example.deploy.domain.campaign.exception;

import com.example.deploy.global.exception.ErrorCode;
import com.example.deploy.global.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "체험단 에러")
public class CampaignException extends GlobalException {

    private final ErrorCode errorCode;

    public CampaignException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    @Override
    public void exceptionHandling() {
        log.error(errorCode.getMsg() + " : " + errorCode.getStatus().value());
    }
}
