package com.moassam.credit.adapter.web.dto;

public record DevCreditChargeResponse(
        int balance
) {
    public static DevCreditChargeResponse from(int balance) {
        return new DevCreditChargeResponse(balance);
    }
}
