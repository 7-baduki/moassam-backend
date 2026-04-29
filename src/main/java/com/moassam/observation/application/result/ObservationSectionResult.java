package com.moassam.observation.application.result;

import com.moassam.observation.domain.ObservationSection;
import com.moassam.observation.domain.SectionType;

public record ObservationSectionResult(
        Long sectionId,
        SectionType type,
        String content,
        boolean edited
) {
    public static ObservationSectionResult from(ObservationSection section) {
        return new ObservationSectionResult(
                section.getId(),
                section.getType(),
                section.getContent(),
                section.isEdited()
        );
    }
}
