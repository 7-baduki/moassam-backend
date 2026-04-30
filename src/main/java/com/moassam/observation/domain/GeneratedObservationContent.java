package com.moassam.observation.domain;

import java.util.List;

public record GeneratedObservationContent(
        List<ObservationSection> sections,
        String summaryContent,
        String phoneConsultationContent
) {
}
