package com.moassam.credit.domain;

import com.moassam.credit.exception.CreditErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreditWalletTest {

    @Test
    void create() {
        CreditWallet wallet = CreditWallet.create(1L, LocalDate.of(2026, 5, 6));

        assertThat(wallet.getUserId()).isEqualTo(1L);
        assertThat(wallet.getBalance()).isEqualTo(CreditPolicy.DAILY_BASE_CREDITS);
        assertThat(wallet.getDailyBonusChargedAmount()).isZero();
        assertThat(wallet.getLastResetDate()).isEqualTo(LocalDate.of(2026, 5, 6));
    }

    @Test
    void use() {
        CreditWallet wallet = CreditWallet.create(1L, LocalDate.of(2026, 5, 6));

        wallet.use(1);

        assertThat(wallet.getBalance()).isEqualTo(9);
    }

    @Test
    void use_notEnough() {
        CreditWallet wallet = CreditWallet.create(1L, LocalDate.of(2026, 5, 6));

        assertThatThrownBy(() -> wallet.use(11))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CreditErrorCode.CREDIT_NOT_ENOUGH);
    }

    @Test
    void chargeWithDailyBonus() {
        CreditWallet wallet = CreditWallet.create(1L, LocalDate.of(2026, 5, 6));

        int chargedAmountMoabang = wallet.chargeWithDailyBonus(CreditPolicy.MOABANG_POST_REWARD);

        assertThat(chargedAmountMoabang).isEqualTo(3);
        assertThat(wallet.getBalance()).isEqualTo(13);
        assertThat(wallet.getDailyBonusChargedAmount()).isEqualTo(3);

        int chargedAmountFree = wallet.chargeWithDailyBonus(CreditPolicy.FREE_POST_REWARD);
        assertThat(chargedAmountFree).isEqualTo(1);
        assertThat(wallet.getBalance()).isEqualTo(14);
        assertThat(wallet.getDailyBonusChargedAmount()).isEqualTo(4);

    }

    @Test
    void chargeWithDailyBonus_limit() {
        CreditWallet wallet = CreditWallet.create(1L, LocalDate.of(2026, 5, 6));
        ReflectionTestUtils.setField(wallet, "balance", 18);
        ReflectionTestUtils.setField(wallet, "dailyBonusChargedAmount", 8);

        int chargedAmount = wallet.chargeWithDailyBonus(CreditPolicy.MOABANG_POST_REWARD);

        assertThat(chargedAmount).isEqualTo(2);
        assertThat(wallet.getBalance()).isEqualTo(20);
        assertThat(wallet.getDailyBonusChargedAmount()).isEqualTo(10);
    }

    @Test
    void reset() {
        CreditWallet wallet = CreditWallet.create(1L, LocalDate.of(2026, 5, 5));
        wallet.use(5);
        wallet.chargeWithDailyBonus(3);

        wallet.reset(LocalDate.of(2026, 5, 6));

        assertThat(wallet.getBalance()).isEqualTo(10);
        assertThat(wallet.getDailyBonusChargedAmount()).isZero();
        assertThat(wallet.getLastResetDate()).isEqualTo(LocalDate.of(2026, 5, 6));
    }

    @Test
    void shouldReset() {
        CreditWallet wallet = CreditWallet.create(1L, LocalDate.of(2026, 5, 5));

        assertThat(wallet.shouldReset(LocalDate.of(2026, 5, 6))).isTrue();
        assertThat(wallet.shouldReset(LocalDate.of(2026, 5, 5))).isFalse();
    }
}
