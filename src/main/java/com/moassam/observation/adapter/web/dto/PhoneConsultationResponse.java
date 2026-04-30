package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.Observation;

public record PhoneConsultationResponse(
        String summaryContent,
        String phoneConsultationContent,
        boolean derivedContentStale
) {

    public static PhoneConsultationResponse from(Observation observation) {
        return new PhoneConsultationResponse(
                observation.getSummaryContent(),
                observation.getPhoneConsultationContent(),
                observation.isDerivedContentStale()
        );
    }
}
