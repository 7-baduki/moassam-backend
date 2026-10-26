package com.moassam.observation.domain;

import java.util.List;

public record ObservationGeneration(
        String title,
        String summary,
        List<Section> sections,
        String providerResponseId,
        String model,
        Long inputTokens,
        Long outputTokens,
        Long totalTokens
) {

    public ObservationGeneration(String title, String summary, List<Section> sections) {
        this(title, summary, sections, null, null, null, null, null);
    }

    public record Section(SectionType sectionType, String content) {}
}
