package com.moassam.observation.application.provided;

public interface ObservationDeleter {
    void deleteObservation(Long userId, Long observationId);
}
