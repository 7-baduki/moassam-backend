package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.ObservationSection;
import com.moassam.observation.domain.SectionType;

public record ObservationSectionResponse(
        Long sectionId,
        SectionType type,
        String content,
        boolean edited
) {

    public static ObservationSectionResponse from(ObservationSection section) {
        return new ObservationSectionResponse(
                section.getId(),
                section.getType(),
                section.getContent(),
                section.isEdited()
        );
    }
}
