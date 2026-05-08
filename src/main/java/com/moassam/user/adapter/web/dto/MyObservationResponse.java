package com.moassam.user.adapter.web.dto;

import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.CurriculumType;
import com.moassam.observation.domain.Observation;

import java.time.LocalDateTime;

public record MyObservationResponse(
        Long observationId,
        String title,
        Age age,
        CurriculumType curriculumType,
        LocalDateTime createdAt
) {
    public static MyObservationResponse from(Observation observation) {
        return new MyObservationResponse(
                observation.getId(),
                observation.getTitle(),
                observation.getAge(),
                observation.getCurriculumType(),
                observation.getCreatedAt()
        );
    }
}