package com.example.deploy.global.exception;


import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getStatus();

    String getMsg();
}
