package com.moassam.observation.application.provided;

import com.moassam.observation.domain.ObservationDetail;
import com.moassam.observation.domain.ObservationListDetail;

public interface ObservationFinder {
    ObservationDetail getObservation(Long userId, Long observationId);

    ObservationListDetail getObservationList(Long userId, Long cursor, int size);
}
