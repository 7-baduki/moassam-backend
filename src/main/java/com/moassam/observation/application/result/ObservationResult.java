package com.moassam.observation.application.result;

import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Curriculum;
import com.moassam.observation.domain.Observation;
import com.moassam.observation.domain.ObservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ObservationResult(
        Long observationId,
        String memo,
        Age age,
        Curriculum curriculum,
        ObservationStatus status,
        List<KeywordResult> keywords,
        List<ObservationSectionResult> sections,
        String summaryContent,
        String phoneConsultationContent,
        boolean derivedContentStale,
        LocalDateTime savedAt
) {

    public static ObservationResult from(Observation observation) {
        return new ObservationResult(
                observation.getId(),
                observation.getMemo(),
                observation.getAge(),
                observation.getCurriculum(),
                observation.getStatus(),
                observation.getKeywords().stream()
                        .map(KeywordResult::from)
                        .toList(),
                observation.getSections().stream()
                        .map(ObservationSectionResult::from)
                        .toList(),
                observation.getSummaryContent(),
                observation.getPhoneConsultationContent(),
                observation.isDerivedContentStale(),
                observation.getSavedAt()
        );
    }
}
