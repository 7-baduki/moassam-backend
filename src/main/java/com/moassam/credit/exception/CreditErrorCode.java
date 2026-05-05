package com.moassam.credit.exception;

import com.moassam.shared.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CreditErrorCode implements ErrorCode {
    CREDIT_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "횟수가 부족합니다."),
    CREDIT_WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "CreditWallet이 존재하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String detail;
}
