package com.moassam.user.application;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
public class UserActivityRecorder {

    private final UserActivityWriter writer;
    private final Counter failureCounter;

    public UserActivityRecorder(UserActivityWriter writer, MeterRegistry meterRegistry) {
        this.writer = writer;
        this.failureCounter = Counter.builder("moassam.analytics.recorder.failures")
                .tag("metric", "user_activity")
                .register(meterRegistry);
    }

    public void recordActive(Long userId) {
        safelyRecord("active", userId, writer::recordActive);
    }

    public void recordObservationUsed(Long userId) {
        safelyRecord("observation_used", userId, writer::recordObservationUsed);
    }

    public void recordCommunityUsed(Long userId) {
        safelyRecord("community_used", userId, writer::recordCommunityUsed);
    }

    private void safelyRecord(String operation, Long userId, Consumer<Long> action) {
        try {
            action.accept(userId);
        } catch (RuntimeException exception) {
            failureCounter.increment();
            log.warn(
                    "analytics_activity_record_failed operation={} userId={} errorType={}",
                    operation,
                    userId,
                    exception.getClass().getSimpleName()
            );
        }
    }
}
