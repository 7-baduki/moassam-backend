package com.moassam.user.application;

import com.moassam.user.application.required.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserActivityWriter {

    private final UserActivityRepository repository;
    private final Clock clock;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordActive(Long userId) {
        LocalDateTime occurredAt = LocalDateTime.now(clock);
        repository.recordActive(occurredAt.toLocalDate(), userId, occurredAt);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordObservationUsed(Long userId) {
        LocalDateTime occurredAt = LocalDateTime.now(clock);
        repository.recordObservationUsed(occurredAt.toLocalDate(), userId, occurredAt);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void recordObservationCreated(Long userId) {
        LocalDateTime occurredAt = LocalDateTime.now(clock);
        repository.recordObservationCreated(occurredAt.toLocalDate(), userId, occurredAt);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void recordObservationRegenerated(Long userId) {
        LocalDateTime occurredAt = LocalDateTime.now(clock);
        repository.recordObservationRegenerated(occurredAt.toLocalDate(), userId, occurredAt);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordCommunityUsed(Long userId) {
        LocalDateTime occurredAt = LocalDateTime.now(clock);
        repository.recordCommunityUsed(occurredAt.toLocalDate(), userId, occurredAt);
    }
}
