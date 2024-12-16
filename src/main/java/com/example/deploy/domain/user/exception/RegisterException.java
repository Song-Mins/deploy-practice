package com.example.deploy.domain.user.exception;


import com.example.deploy.global.exception.ErrorCode;
import com.example.deploy.global.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "회원가입 에러")
public class RegisterException extends GlobalException {

    private final ErrorCode errorCode;

    public RegisterException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    @Override
    public void exceptionHandling() {
        log.error(errorCode.getMsg() + " : " + errorCode.getStatus().value());
    }
}
