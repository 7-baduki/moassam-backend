package com.moassam.observation.application.result;

public record PhoneConsultationResult(
        String summaryContent,
        String phoneConsultationContent,
        boolean derivedContentStale
) {
}
