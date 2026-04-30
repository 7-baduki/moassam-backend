package com.moassam.observation.application.provided;

import com.moassam.observation.domain.Observation;
import com.moassam.observation.domain.ObservationSection;

public interface ObservationFinder {
    Observation get(Long userId, Long observationId);
    ObservationSection getSection(Long userId, Long observationId, Long sectionId);

}
