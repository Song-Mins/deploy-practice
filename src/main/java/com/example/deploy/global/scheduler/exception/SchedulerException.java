package com.example.deploy.global.scheduler.exception;


import com.example.deploy.global.exception.ErrorCode;
import com.example.deploy.global.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "스케줄러 에러")
public class SchedulerException extends GlobalException {

    private final ErrorCode errorCode;

    public SchedulerException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    @Override
    public void exceptionHandling() {
        log.error(errorCode.getMsg() + " : " + errorCode.getStatus().value());
    }
}
