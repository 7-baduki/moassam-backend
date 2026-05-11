package com.moassam.credit.application.dev;

import com.moassam.credit.application.provided.DevCreditCharger;
import com.moassam.credit.application.required.CreditWalletRepository;
import com.moassam.credit.domain.CreditWallet;
import com.moassam.credit.exception.CreditErrorCode;
import com.moassam.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class DevCreditService implements DevCreditCharger {

    private final CreditWalletRepository creditWalletRepository;

    @Override
    public int chargeDevCredit(Long userId) {
        CreditWallet creditWallet = creditWalletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(CreditErrorCode.CREDIT_WALLET_NOT_FOUND));

        creditWallet.devCharge(20);

        return creditWallet.getBalance();
    }
}
