package com.moassam.credit.adapter.web.dto;

import com.moassam.credit.domain.CreditWallet;

import java.time.LocalDate;

public record CreditWalletResponse(
        Long userId,
        int balance,
        int dailyBonusChargedAmount,
        LocalDate lastResetDate
) {
    public static CreditWalletResponse from(CreditWallet wallet) {
        return new CreditWalletResponse(
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getDailyBonusChargedAmount(),
                wallet.getLastResetDate()
        );
    }
}
