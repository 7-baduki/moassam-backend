package com.moassam.observation.adapter.integration.ai.dto;

import java.util.List;

public record OpenAiObservationResponse(
        List<OpenAiObservationSectionResponse> sections,
        String summaryContent,
        String phoneConsultationContent
) {
}
