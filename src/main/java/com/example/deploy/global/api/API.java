package com.example.deploy.global.api;


import com.example.deploy.global.exception.GlobalException;
import com.example.deploy.global.exception.GlobalResponse;
import lombok.Data;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Data
public class API {

    public static ResponseEntity OK(Object data) {
        return ResponseEntity.ok(data);
    }

    public static ResponseEntity OK() {
        return ResponseEntity.ok(new GlobalResponse("성공", HttpStatus.OK));
    }

    public static ResponseEntity OK(String msg) {
        return ResponseEntity.ok(new GlobalResponse(msg, HttpStatus.OK));
    }

    public static ResponseEntity ERROR(GlobalException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(ex.getGlobalResponse());
    }

    public static ResponseEntity ERROR(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(400)
                .body(new GlobalResponse("잘못된 인자값 입니다.", HttpStatus.valueOf(400)));
    }

    public static ResponseEntity ERROR(AccessDeniedException ex) {
        return ResponseEntity.status(403)
                .body(new GlobalResponse("해당 기능에 대한 권한이 없습니다.", HttpStatus.valueOf(403)));
    }

    public static ResponseEntity ERROR(SchedulerException ex) {
        return ResponseEntity.status(403)
            .body(new GlobalResponse("스케줄러 동적 변경에 실패하였습니다.", HttpStatus.valueOf(403)));
    }
}
