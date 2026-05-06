package com.moassam.credit.application.provided;

public interface CreditUser {
    void useForCreateObservation(Long userId, Long observationId);
    void useForRegenerateObservation(Long userId, Long observationId);
}
