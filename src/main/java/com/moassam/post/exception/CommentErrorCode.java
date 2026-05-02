package com.moassam.post.exception;

import com.moassam.shared.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    COMMENT_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "댓글 내용을 입력해주세요.");

    private final HttpStatus status;
    private final String detail;
}
