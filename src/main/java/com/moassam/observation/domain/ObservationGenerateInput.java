package com.moassam.observation.domain;

import java.util.List;

public record ObservationGenerateInput(
        String memo,
        Age age,
        Curriculum curriculum,
        List<SectionType> sectionTypes,
        List<KeywordInput> keywords
) {
}
