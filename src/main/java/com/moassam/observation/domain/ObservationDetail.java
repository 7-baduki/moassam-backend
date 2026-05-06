package com.moassam.observation.domain;

import java.util.List;

public record ObservationDetail(
        Observation observation,
        List<ObservationSection> sections
) {
}
