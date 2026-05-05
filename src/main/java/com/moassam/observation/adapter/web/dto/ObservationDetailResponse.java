package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.*;

import java.util.List;

public record ObservationDetailResponse(
        Long observationId,
        String title,
        String summary,
        List<ObservationSectionResponse> sections
) {

    public static ObservationDetailResponse from(ObservationDetail observationDetail) {
        Observation observation = observationDetail.observation();
        List<ObservationSection> sections = observationDetail.sections();

        return new ObservationDetailResponse(
                observation.getId(),
                observation.getTitle(),
                observation.getSummary(),
                sections.stream()
                        .map(ObservationSectionResponse::from)
                        .toList()
        );
    }
}