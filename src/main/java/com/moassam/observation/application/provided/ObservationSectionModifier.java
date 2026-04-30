package com.moassam.observation.application.provided;

import com.moassam.observation.domain.ObservationSection;
import com.moassam.observation.domain.SectionUpdateInput;

public interface ObservationSectionModifier {
    ObservationSection updateSection(Long userId, Long observationId, Long sectionId, SectionUpdateInput input);

}
