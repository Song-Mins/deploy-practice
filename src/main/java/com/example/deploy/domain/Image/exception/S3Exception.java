package com.example.deploy.domain.Image.exception;


import com.example.deploy.domain.Image.exception.errortype.S3ErrorCode;
import com.example.deploy.global.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3Exception extends GlobalException {
    private final S3ErrorCode errorCode;

    public S3Exception(S3ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    @Override
    public void exceptionHandling() {
        log.info(errorCode.getStatus() + ": " + errorCode.getMsg());
    }
}
