package com.example.deploy.global.scheduler.exception.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SchedulerErrorCode implements ErrorCode {
    SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "동기화 실패: 데이터를 동기화하는 동안 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String msg;
}
