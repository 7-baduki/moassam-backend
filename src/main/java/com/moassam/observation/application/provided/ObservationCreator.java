package com.moassam.observation.application.provided;

import com.moassam.observation.domain.ObservationCreateRequest;

public interface ObservationCreator {
    Long createObservation(Long userId, ObservationCreateRequest request);
}
