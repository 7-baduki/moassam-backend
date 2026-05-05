package com.moassam.observation.domain;

public record ObservationReference(
        CurriculumType curriculumType,
        SectionType sectionType,
        String content
) {
}
