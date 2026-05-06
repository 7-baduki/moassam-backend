package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.Observation;

public record ObservationListItemResponse(
        Long observationId,
        String title
) {
    public static ObservationListItemResponse from(Observation observation) {
        return new ObservationListItemResponse(
                observation.getId(),
                observation.getTitle()
        );
    }
}
