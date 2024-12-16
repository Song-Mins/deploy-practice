package com.example.deploy.domain.application.exception.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApplicationErrorCode implements ErrorCode {
    FAIL_CANCEL(HttpStatus.BAD_REQUEST, "신청 취소에 실패하였습니다."),
    NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "해당 id의 신청이 없습니다.");

    private final HttpStatus status;
    private final String msg;
}
