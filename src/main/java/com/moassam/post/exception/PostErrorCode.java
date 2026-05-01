package com.moassam.post.exception;

import com.moassam.shared.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {
    POST_INVALID_CATEGORY_OPTION(HttpStatus.BAD_REQUEST, "게시판 카테고리 옵션이 올바르지 않습니다."),
    POST_UPLOAD_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "업로드 용량은 10MB를 초과할 수 없습니다."),
    POST_INVALID_EDITOR_IMAGE(HttpStatus.BAD_REQUEST, "에디터 이미지는 jpg, jpeg, png 파일만 업로드할 수 있습니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    POST_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

    POST_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다.");

    private final HttpStatus status;
    private final String detail;
}
