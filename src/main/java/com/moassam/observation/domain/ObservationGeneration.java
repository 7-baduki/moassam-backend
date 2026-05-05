package com.moassam.observation.domain;

import java.util.List;

public record ObservationGeneration(
        String title,
        String summary,
        List<Section> sections
) {

    public record Section(SectionType sectionType, String content) {}
}
