package com.example.deploy.domain.user.exception;


import com.example.deploy.global.exception.ErrorCode;
import com.example.deploy.global.exception.GlobalException;

public class UserException extends GlobalException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public void exceptionHandling() {}
}
