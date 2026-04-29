package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.application.result.ObservationSectionResult;
import com.moassam.observation.domain.SectionType;

public record ObservationSectionResponse(
        Long sectionId,
        SectionType type,
        String content,
        boolean edited
) {

    public static ObservationSectionResponse from(ObservationSectionResult result) {
        return new ObservationSectionResponse(
                result.sectionId(),
                result.type(),
                result.content(),
                result.edited()
        );
    }
}
