package com.moassam.observation.application.provided;

import com.moassam.observation.domain.Observation;
import com.moassam.observation.domain.ObservationGenerateInput;

public interface ObservationCreator {
    Observation generateObservation(Long userId, ObservationGenerateInput input);
}
