package com.example.deploy.domain.user.exception.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "Expired JWT token, 만료된 JWT token 입니다."),
    UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다."),
    JWT_EMPTY(HttpStatus.UNAUTHORIZED, "JWT claims is empty, 잘못된 JWT 토큰 입니다."),
    LOGIN_ERROR(HttpStatus.BAD_REQUEST, "Login 실패 하였습니다."),
    LOGOUT_ERROR(HttpStatus.BAD_REQUEST, "Logout 실패 하였습니다."),
    NOT_FOUND_TOKEN(HttpStatus.BAD_REQUEST, "토큰을 찾을 수 없습니다."),
    OAUTH_TOKEN_ISSUANCE_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "OAuth 토큰 발급 요청에 실패하였습니다.");

    private final HttpStatus status;
    private final String msg;
}
