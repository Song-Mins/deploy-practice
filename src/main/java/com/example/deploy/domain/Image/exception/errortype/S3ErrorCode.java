package com.example.deploy.domain.Image.exception.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements ErrorCode {
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "파일 업로드 실패"),
    INVALID_IMAGE_FILE(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE, "유효하지 않은 이미지 파일입니다. 허용된 파일 형식: jpg, jpeg, png");

    private final HttpStatus status;
    private final String msg;
}
