package com.moassam.observation.domain;

import java.util.List;

public record ObservationRegenerateInput(
        String memo,
        Age age,
        Curriculum curriculum,
        List<SectionType> sectionTypes,
        List<KeywordInput> keywords
) {
}
