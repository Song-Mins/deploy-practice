package com.example.deploy.domain.post.exception;


import com.example.deploy.global.exception.ErrorCode;
import com.example.deploy.global.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "게시글 에러")
public class PostException extends GlobalException {

    private final ErrorCode errorCode;

    public PostException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    @Override
    public void exceptionHandling() {
        log.error(errorCode.getMsg() + " : " + errorCode.getStatus().value());
    }
}
