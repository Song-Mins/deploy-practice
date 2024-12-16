package com.example.deploy.global.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class GlobalException extends RuntimeException {

    @Getter private final ErrorCode errorCode;

    public abstract void exceptionHandling();

    public GlobalResponse getGlobalResponse() {
        return new GlobalResponse(errorCode.getMsg(), errorCode.getStatus());
    }
}
