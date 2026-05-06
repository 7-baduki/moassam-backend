package com.moassam.observation.domain;

import java.util.List;

public record ObservationListDetail(
        List<Observation> observations,
        Long nextCursor,
        boolean hasNext
) {
}
