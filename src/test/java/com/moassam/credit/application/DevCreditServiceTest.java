package com.moassam.credit.application;

import com.moassam.credit.application.dev.DevCreditService;
import com.moassam.credit.application.required.CreditWalletRepository;
import com.moassam.credit.domain.CreditWallet;
import com.moassam.credit.exception.CreditErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class DevCreditServiceTest {

    private final CreditWalletRepository creditWalletRepository = mock(CreditWalletRepository.class);

    private final DevCreditService devCreditService = new DevCreditService(creditWalletRepository);

    @Test
    void chargeDevCredit() {
        CreditWallet wallet = CreditWallet.create(1L, LocalDate.of(2026, 5, 6));

        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.of(wallet));

        int balance = devCreditService.chargeDevCredit(1L);

        assertThat(balance).isEqualTo(30);
        assertThat(wallet.getDailyBonusChargedAmount()).isZero();
        then(creditWalletRepository).should().findByUserIdForUpdate(1L);
    }

    @Test
    void chargeDevCredit_notFound() {
        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> devCreditService.chargeDevCredit(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CreditErrorCode.CREDIT_WALLET_NOT_FOUND);
    }
}