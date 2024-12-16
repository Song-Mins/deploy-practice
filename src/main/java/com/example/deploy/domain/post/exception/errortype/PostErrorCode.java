package com.example.deploy.domain.post.exception.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements ErrorCode {

    // 게시글 공통
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "게시글을 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "작성자만 수정 및 삭제할 수 있습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "카테고리를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String msg;
}
