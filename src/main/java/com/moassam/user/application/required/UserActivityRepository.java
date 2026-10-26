package com.moassam.user.application.required;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserActivityRepository {

    void recordActive(LocalDate activityDate, Long userId, LocalDateTime occurredAt);

    void recordObservationCreated(LocalDate activityDate, Long userId, LocalDateTime occurredAt);

    void recordObservationUsed(LocalDate activityDate, Long userId, LocalDateTime occurredAt);

    void recordObservationRegenerated(LocalDate activityDate, Long userId, LocalDateTime occurredAt);

    void recordCommunityUsed(LocalDate activityDate, Long userId, LocalDateTime occurredAt);
}
