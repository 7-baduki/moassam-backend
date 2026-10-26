package com.moassam.user.application.required;

import com.moassam.user.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserLifecycleEventRepository extends Repository<User, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO user_lifecycle_events(user_id, event_type, had_meaningful_use, occurred_at)
            VALUES (:userId, :eventType, :hadMeaningfulUse, :occurredAt)
            """, nativeQuery = true)
    void append(
            @Param("userId") Long userId,
            @Param("eventType") String eventType,
            @Param("hadMeaningfulUse") Boolean hadMeaningfulUse,
            @Param("occurredAt") LocalDateTime occurredAt
    );
}
