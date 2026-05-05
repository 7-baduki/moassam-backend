package com.moassam.credit.application;

import com.moassam.credit.application.required.CreditWalletRepository;
import com.moassam.credit.domain.CreditPolicy;
import com.moassam.credit.domain.CreditWallet;
import com.moassam.credit.exception.CreditErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class CreditServiceTest {

    private final CreditWalletRepository creditWalletRepository = mock(CreditWalletRepository.class);

    private final CreditService creditService = new CreditService(creditWalletRepository);

    @Test
    void getWallet() {
        CreditWallet wallet = wallet(1L, 1L, 10, 0, LocalDate.now());

        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.of(wallet));

        CreditWallet result = creditService.getWallet(1L);

        assertThat(result).isEqualTo(wallet);
        then(creditWalletRepository).should().findByUserIdForUpdate(1L);
    }

    @Test
    void getWallet_resetIfNeeded() {
        CreditWallet wallet = wallet(1L, 1L, 3, 5, LocalDate.now().minusDays(1));

        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.of(wallet));

        CreditWallet result = creditService.getWallet(1L);

        assertThat(result.getBalance()).isEqualTo(CreditPolicy.DAILY_BASE_CREDITS);
        assertThat(result.getDailyBonusChargedAmount()).isZero();
        assertThat(result.getLastResetDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void getWallet_notFound() {
        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> creditService.getWallet(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CreditErrorCode.CREDIT_WALLET_NOT_FOUND);
    }

    @Test
    void createInitialWallet() {
        given(creditWalletRepository.save(any(CreditWallet.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        creditService.createInitialWallet(1L);

        then(creditWalletRepository).should().save(any(CreditWallet.class));
    }

    @Test
    void useForCreateObservation() {
        CreditWallet wallet = wallet(1L, 1L, 10, 0, LocalDate.now());

        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.of(wallet));

        creditService.useForCreateObservation(1L, 1L);

        assertThat(wallet.getBalance()).isEqualTo(9);
    }

    @Test
    void useForRegenerateObservation() {
        CreditWallet wallet = wallet(1L, 1L, 10, 0, LocalDate.now());

        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.of(wallet));

        creditService.useForRegenerateObservation(1L, 1L);

        assertThat(wallet.getBalance()).isEqualTo(9);
    }

    @Test
    void chargeForMoabangPost() {
        CreditWallet wallet = wallet(1L, 1L, 10, 0, LocalDate.now());

        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.of(wallet));

        creditService.chargeForMoabangPost(1L, 10L);

        assertThat(wallet.getBalance()).isEqualTo(13);
        assertThat(wallet.getDailyBonusChargedAmount()).isEqualTo(3);
    }

    @Test
    void chargeForMoabangPost_limit() {
        CreditWallet wallet = wallet(1L, 1L, 18, 8, LocalDate.now());

        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.of(wallet));

        creditService.chargeForMoabangPost(1L, 10L);

        assertThat(wallet.getBalance()).isEqualTo(20);
        assertThat(wallet.getDailyBonusChargedAmount()).isEqualTo(10);
    }

    @Test
    void chargeForFreePost() {
        CreditWallet wallet = wallet(1L, 1L, 10, 0, LocalDate.now());

        given(creditWalletRepository.findByUserIdForUpdate(1L))
                .willReturn(Optional.of(wallet));

        creditService.chargeForFreePost(1L, 10L);

        assertThat(wallet.getBalance()).isEqualTo(11);
        assertThat(wallet.getDailyBonusChargedAmount()).isEqualTo(1);
    }

    private CreditWallet wallet(
            Long id,
            Long userId,
            int balance,
            int dailyBonusChargedAmount,
            LocalDate lastResetDate
    ) {
        CreditWallet creditWallet = CreditWallet.create(userId, lastResetDate);

        ReflectionTestUtils.setField(creditWallet, "id", id);
        ReflectionTestUtils.setField(creditWallet, "balance", balance);
        ReflectionTestUtils.setField(creditWallet, "dailyBonusChargedAmount", dailyBonusChargedAmount);
        ReflectionTestUtils.setField(creditWallet, "lastResetDate", lastResetDate);

        return creditWallet;
    }
}
