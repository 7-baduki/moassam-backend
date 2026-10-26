package com.moassam.observation.application;

import com.moassam.observation.domain.ObservationGeneration;
import com.moassam.observation.domain.ObservationGenerationOperation;
import com.moassam.observation.domain.ObservationGenerationUsage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ObservationGenerationUsageRecorder {

    private final ObservationGenerationUsageWriter writer;
    private final Clock clock;
    private final Counter failureCounter;

    public ObservationGenerationUsageRecorder(
            ObservationGenerationUsageWriter writer,
            Clock clock,
            MeterRegistry meterRegistry
    ) {
        this.writer = writer;
        this.clock = clock;
        this.failureCounter = Counter.builder("moassam.analytics.recorder.failures")
                .tag("metric", "ai_usage")
                .register(meterRegistry);
    }

    public void recordProviderSuccess(
            String correlationId,
            Long userId,
            ObservationGenerationOperation operation,
            ObservationGeneration generation,
            long durationMs
    ) {
        safely(() -> writer.insert(new ObservationGenerationUsage(
                correlationId,
                userId,
                operation,
                "SUCCESS",
                "PENDING",
                generation.model(),
                generation.inputTokens(),
                generation.outputTokens(),
                generation.totalTokens(),
                durationMs,
                null,
                LocalDateTime.now(clock)
        )), "insert_success", correlationId);
    }

    public void recordProviderFailure(
            String correlationId,
            Long userId,
            ObservationGenerationOperation operation,
            long durationMs
    ) {
        safely(() -> writer.insert(new ObservationGenerationUsage(
                correlationId,
                userId,
                operation,
                "FAILED",
                "PROVIDER_FAILED",
                null,
                null,
                null,
                null,
                durationMs,
                "GENERATION_FAILED",
                LocalDateTime.now(clock)
        )), "insert_failure", correlationId);
    }

    public void markSaved(String correlationId) {
        safely(() -> writer.updateBusinessStatus(correlationId, "SAVED"), "mark_saved", correlationId);
    }

    public void markPersistenceFailed(String correlationId) {
        safely(() -> writer.updateBusinessStatus(correlationId, "PERSISTENCE_FAILED"),
                "mark_persistence_failed", correlationId);
    }

    private void safely(Runnable action, String operation, String correlationId) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            failureCounter.increment();
            log.warn(
                    "analytics_ai_usage_record_failed operation={} correlationId={} errorType={}",
                    operation,
                    correlationId,
                    exception.getClass().getSimpleName()
            );
        }
    }
}
