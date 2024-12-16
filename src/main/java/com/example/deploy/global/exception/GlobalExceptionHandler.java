package com.example.deploy.global.exception;


import com.example.deploy.global.api.API;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j(topic = "exceptionHandler")
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity globalError(GlobalException ex) {
        ex.exceptionHandling();
        return API.ERROR(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity validationError(MethodArgumentNotValidException ex) {
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            log.error(fieldError.getDefaultMessage());
        }
        return API.ERROR(ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedException(AccessDeniedException ex) {
        return API.ERROR(ex);
    }

    @ExceptionHandler(SchedulerException.class)
    public ResponseEntity<ErrorResponse> SchedulerException(SchedulerException ex) {
        return API.ERROR(ex);
    }
}
