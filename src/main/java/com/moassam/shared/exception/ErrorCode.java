package com.moassam.shared.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();
    String getDetail();
    String name();
}
