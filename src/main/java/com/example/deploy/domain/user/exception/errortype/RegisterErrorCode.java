package com.example.deploy.domain.user.exception.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RegisterErrorCode implements ErrorCode {
    EMAIL_SAME(HttpStatus.BAD_REQUEST, "이메일이 중복되는 값이 존재합니다."),
    FAIL_IMP_ID(HttpStatus.BAD_REQUEST, "잘못된 ImpID 입니다."),
    FAIL_IMP_NAME_NOT_SAME(HttpStatus.BAD_REQUEST, "유효하지 않는 이름입니다."),
    NOT_FOUND_OAUTH_TYPE(HttpStatus.BAD_REQUEST, "찾을수 없는 타입입니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "회원가입이 안된 유저입니다.");

    private final HttpStatus status;
    private final String msg;
}
