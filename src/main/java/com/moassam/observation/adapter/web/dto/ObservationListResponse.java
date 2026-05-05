package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.ObservationListDetail;

import java.util.List;

public record ObservationListResponse(
        List<ObservationListItemResponse> items,
        Long nextCursor,
        boolean hasNext
) {

    public static ObservationListResponse from(ObservationListDetail observationListDetail) {
        return new ObservationListResponse(
                observationListDetail.observations().stream()
                        .map(ObservationListItemResponse::from)
                        .toList(),
                observationListDetail.nextCursor(),
                observationListDetail.hasNext()
        );
    }
}
