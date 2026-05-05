package com.moassam.observation.exception;

import com.moassam.shared.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ObservationErrorCode implements ErrorCode {
    OBSERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "관찰일지를 찾을 수 없습니다."),
    OBSERVATION_SECTION_REQUIRED(HttpStatus.BAD_REQUEST, "관찰 영역은 필수입니다."),
    OBSERVATION_SITUATION_REQUIRED(HttpStatus.BAD_REQUEST, "관찰 내용은 필수입니다."),
    OBSERVATION_AGE_REQUIRED(HttpStatus.BAD_REQUEST, "연령은 필수입니다.");

    private final HttpStatus status;
    private final String detail;
}
