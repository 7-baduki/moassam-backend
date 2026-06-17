package com.moassam.admin.exception;

import com.moassam.shared.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminAuthErrorCode implements ErrorCode {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "관리자 아이디 또는 비밀번호가 올바르지 않습니다."),
    ADMIN_DISABLED(HttpStatus.FORBIDDEN, "비활성화된 관리자 계정입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 관리자 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 관리자 토큰입니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "관리자 계정을 찾을 수 없습니다."),;

    private final HttpStatus status;
    private final String detail;
}
