package com.example.deploy.domain.like.exception.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LikeErrorCode implements ErrorCode {
    ;

    private final HttpStatus status;
    private final String msg;
}
