package com.example.deploy.domain.comment.excepiton.errortype;


import com.example.deploy.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    COMMENT_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN, "댓글 작성자가 일치하지 않습니다."),
    COMMENT_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "댓글 수정에 실패했습니다."),
    COMMENT_CREATE_FAILED(HttpStatus.BAD_REQUEST, "댓글 생성에 실패했습니다."),
    COMMENT_DELETE_FAILED(HttpStatus.BAD_REQUEST, "댓글 삭제에 실패했습니다.");

    private final HttpStatus status;
    private final String msg;
}
