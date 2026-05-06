package com.moassam.credit.application.provided;

public interface CreditCharger {
    void createInitialWallet(Long userId);
    void chargeForMoabangPost(Long userId, Long postId);
    void chargeForFreePost(Long userId, Long postId);
}
