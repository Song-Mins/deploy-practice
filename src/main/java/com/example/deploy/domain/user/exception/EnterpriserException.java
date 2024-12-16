package com.example.deploy.domain.user.exception;


import com.example.deploy.global.exception.ErrorCode;
import com.example.deploy.global.exception.GlobalException;

public class EnterpriserException extends GlobalException {

    public EnterpriserException(ErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public void exceptionHandling() {}
}
