package com.moassam.credit.application;

import com.moassam.credit.application.provided.CreditCharger;
import com.moassam.credit.application.provided.CreditFinder;
import com.moassam.credit.application.provided.CreditUser;
import com.moassam.credit.application.required.CreditWalletRepository;
import com.moassam.credit.domain.CreditPolicy;
import com.moassam.credit.domain.CreditWallet;
import com.moassam.credit.exception.CreditErrorCode;
import com.moassam.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional
@RequiredArgsConstructor
@Service
public class CreditService implements CreditFinder, CreditUser, CreditCharger {

    private final CreditWalletRepository creditWalletRepository;

    @Override
    public CreditWallet getWallet(Long userId) {
        return getOrResetWalletForUpdate(userId);
    }

    @Override
    public void createInitialWallet(Long userId) {
        CreditWallet wallet = CreditWallet.create(userId, LocalDate.now());
        creditWalletRepository.save(wallet);
    }

    @Override
    public void useForCreateObservation(Long userId, Long observationId) {
        CreditWallet wallet = getOrResetWalletForUpdate(userId);
        wallet.use(CreditPolicy.OBSERVATION_CREATE_COST);
    }

    @Override
    public void useForRegenerateObservation(Long userId, Long observationId) {
        CreditWallet wallet = getOrResetWalletForUpdate(userId);
        wallet.use(CreditPolicy.OBSERVATION_REGENERATE_COST);
    }

    @Override
    public void chargeForMoabangPost(Long userId, Long postId) {
        CreditWallet wallet = getOrResetWalletForUpdate(userId);
        wallet.chargeWithDailyBonus(CreditPolicy.MOABANG_POST_REWARD);
    }

    @Override
    public void chargeForFreePost(Long userId, Long postId) {
        CreditWallet wallet = getOrResetWalletForUpdate(userId);
        wallet.chargeWithDailyBonus(CreditPolicy.FREE_POST_REWARD);
    }

    private CreditWallet getOrResetWalletForUpdate(Long userId) {
        CreditWallet creditWallet = creditWalletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(CreditErrorCode.CREDIT_WALLET_NOT_FOUND));

        LocalDate today = LocalDate.now();

        if (creditWallet.shouldReset(today)) {
            creditWallet.reset(today);
        }

        return creditWallet;
    }
}
