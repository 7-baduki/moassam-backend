package com.moassam.observation.domain;

import java.time.LocalDateTime;

public record ObservationGenerationUsage(
        String correlationId,
        Long userId,
        ObservationGenerationOperation operation,
        String providerStatus,
        String businessStatus,
        String model,
        Long inputTokens,
        Long outputTokens,
        Long totalTokens,
        long durationMs,
        String errorCategory,
        LocalDateTime occurredAt
) {
}
