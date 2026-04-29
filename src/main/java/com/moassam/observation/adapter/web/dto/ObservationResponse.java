package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.application.result.ObservationResult;
import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Curriculum;
import com.moassam.observation.domain.ObservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ObservationResponse(
        Long observationId,
        String memo,
        Age age,
        Curriculum curriculum,
        ObservationStatus status,
        List<KeywordResponse> keywords,
        List<ObservationSectionResponse> sections,
        String summaryContent,
        String phoneConsultationContent,
        boolean derivedContentStale,
        LocalDateTime savedAt
) {

    public static ObservationResponse from(ObservationResult result) {
        return new ObservationResponse(
                result.observationId(),
                result.memo(),
                result.age(),
                result.curriculum(),
                result.status(),
                result.keywords().stream()
                        .map(KeywordResponse::from)
                        .toList(),
                result.sections().stream()
                        .map(ObservationSectionResponse::from)
                        .toList(),
                result.summaryContent(),
                result.phoneConsultationContent(),
                result.derivedContentStale(),
                result.savedAt()
        );
    }
}
