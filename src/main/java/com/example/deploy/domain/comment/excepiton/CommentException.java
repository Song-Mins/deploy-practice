package com.example.deploy.domain.comment.excepiton;

import com.example.deploy.global.exception.ErrorCode;
import com.example.deploy.global.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommentException extends GlobalException {

    private final ErrorCode errorCode;

    public CommentException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    @Override
    public void exceptionHandling() {
        log.info(errorCode.getStatus() + ": " + errorCode.getMsg());
    }
}
