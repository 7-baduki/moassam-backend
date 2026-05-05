package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.ObservationSection;
import com.moassam.observation.domain.SectionType;

public record ObservationSectionResponse(
        SectionType sectionType,
        String content
) {
    public static ObservationSectionResponse from(ObservationSection section) {
        return new ObservationSectionResponse(
                section.getSectionType(),
                section.getContent()
        );
    }
}
