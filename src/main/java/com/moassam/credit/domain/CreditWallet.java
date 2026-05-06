package com.moassam.credit.domain;

import com.moassam.credit.exception.CreditErrorCode;
import com.moassam.shared.domain.BaseEntity;
import com.moassam.shared.exception.BusinessException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditWallet extends BaseEntity {

    private Long id;
    private Long userId;
    private int balance;
    private int dailyBonusChargedAmount;
    private LocalDate lastResetDate;

    public static CreditWallet create(Long userId, LocalDate today) {
        CreditWallet creditWallet = new CreditWallet();

        creditWallet.userId = userId;
        creditWallet.balance = CreditPolicy.DAILY_BASE_CREDITS;
        creditWallet.dailyBonusChargedAmount = 0;
        creditWallet.lastResetDate = today;

        return creditWallet;
    }

    public void reset(LocalDate today) {
        this.balance = CreditPolicy.DAILY_BASE_CREDITS;
        this.dailyBonusChargedAmount = 0;
        this.lastResetDate = today;
    }

    public void use(int amount) {
        if (this.balance < amount) {
            throw new BusinessException(CreditErrorCode.CREDIT_NOT_ENOUGH);
        }
        this.balance -= amount;
    }

    public int chargeWithDailyBonus(int rewardAmount) {
        int remainingBonusAmount = CreditPolicy.DAILY_BONUS_CHARGE_LIMIT - this.dailyBonusChargedAmount;
        int actualChargeAmount = Math.clamp(remainingBonusAmount, 0, rewardAmount);

        this.balance += actualChargeAmount;
        this.dailyBonusChargedAmount += actualChargeAmount;

        return actualChargeAmount;
    }

    public boolean shouldReset(LocalDate today) {
        return this.lastResetDate == null || this.lastResetDate.isBefore(today);
    }
}
