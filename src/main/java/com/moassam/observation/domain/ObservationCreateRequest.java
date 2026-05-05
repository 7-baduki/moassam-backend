package com.moassam.observation.domain;

import java.util.List;

public record ObservationCreateRequest(
        Age age,
        List<SectionType> sectionTypes,
        String situation
) {
}
