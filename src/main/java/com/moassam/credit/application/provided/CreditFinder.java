package com.moassam.credit.application.provided;

import com.moassam.credit.domain.CreditWallet;

public interface CreditFinder {
    CreditWallet getWallet(Long userId);
}
