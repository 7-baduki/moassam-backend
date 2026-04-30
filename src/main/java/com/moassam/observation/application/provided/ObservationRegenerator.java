package com.moassam.observation.application.provided;

import com.moassam.observation.domain.Observation;
import com.moassam.observation.domain.ObservationRegenerateInput;
import com.moassam.observation.domain.ObservationSection;
import com.moassam.observation.domain.SectionRegenerateInput;

public interface ObservationRegenerator {
    Observation regenerate(Long userId, Long observationId, ObservationRegenerateInput input);
    ObservationSection regenerateSection(
            Long userId,
            Long observationId,
            Long sectionId,
            SectionRegenerateInput input
    );

}
