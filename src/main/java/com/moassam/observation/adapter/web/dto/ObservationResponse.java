package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Curriculum;
import com.moassam.observation.domain.Observation;
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

    public static ObservationResponse from(Observation observation) {
        return new ObservationResponse(
                observation.getId(),
                observation.getMemo(),
                observation.getAge(),
                observation.getCurriculum(),
                observation.getStatus(),
                observation.getKeywords().stream()
                        .map(KeywordResponse::from)
                        .toList(),
                observation.getSections().stream()
                        .map(ObservationSectionResponse::from)
                        .toList(),
                observation.getSummaryContent(),
                observation.getPhoneConsultationContent(),
                observation.isDerivedContentStale(),
                observation.getSavedAt()
        );
    }
}
