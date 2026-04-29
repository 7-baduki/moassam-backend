package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.application.result.PhoneConsultationResult;

public record PhoneConsultationResponse(
        String summaryContent,
        String phoneConsultationContent,
        boolean derivedContentStale
) {

    public static PhoneConsultationResponse from(PhoneConsultationResult result) {
        return new PhoneConsultationResponse(
                result.summaryContent(),
                result.phoneConsultationContent(),
                result.derivedContentStale()
        );
    }
}
