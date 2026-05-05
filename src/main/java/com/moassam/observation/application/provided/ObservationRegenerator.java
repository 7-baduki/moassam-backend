package com.moassam.observation.application.provided;

import com.moassam.observation.domain.ObservationDetail;

public interface ObservationRegenerator {
    ObservationDetail regenerateObservation(Long userId, Long observationId);
}
