package com.example.deploy.domain.review.exception.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    APPLICATION_NOT_APPROVED(HttpStatus.BAD_REQUEST, "체험단 신청이 승인되지 않았습니다."),
    INVALID_REVIEW_PERIOD(HttpStatus.BAD_REQUEST, "리뷰 작성 가능 기간이 아닙니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "리뷰 신청자가 아닙니다.");

    private final HttpStatus status;
    private final String msg;
}
