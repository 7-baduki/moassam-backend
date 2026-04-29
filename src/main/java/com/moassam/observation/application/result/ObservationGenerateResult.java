package com.moassam.observation.application.result;

import com.moassam.observation.domain.ObservationSection;

import java.util.List;

public record ObservationGenerateResult(
        List<ObservationSection> sections,
        String summaryContent,
        String phoneConsultationContent
) {
}
