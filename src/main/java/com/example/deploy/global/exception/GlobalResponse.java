package com.example.deploy.global.exception;


import org.springframework.http.HttpStatus;

public record GlobalResponse(String msg, HttpStatus status) {}
