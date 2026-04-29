package com.moassam.observation.adapter.integration.ai.dto;

import com.moassam.observation.domain.SectionType;

public record OpenAiObservationSectionResponse(
        SectionType type,
        String content
) {
}
