package com.moassam.observation.adapter.persistence;

import com.moassam.observation.application.required.ObservationGenerationUsageRepository;
import com.moassam.observation.domain.ObservationGenerationUsage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcObservationGenerationUsageRepository implements ObservationGenerationUsageRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcObservationGenerationUsageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(ObservationGenerationUsage usage) {
        jdbcTemplate.update("""
                        INSERT INTO observation_generation_usages(
                            correlation_id, user_id, operation, provider_status, business_status,
                            model, input_tokens, output_tokens, total_tokens, duration_ms,
                            error_category, occurred_at
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                usage.correlationId(),
                usage.userId(),
                usage.operation().name(),
                usage.providerStatus(),
                usage.businessStatus(),
                usage.model(),
                usage.inputTokens(),
                usage.outputTokens(),
                usage.totalTokens(),
                usage.durationMs(),
                usage.errorCategory(),
                usage.occurredAt()
        );
    }

    @Override
    public void updateBusinessStatus(String correlationId, String businessStatus) {
        jdbcTemplate.update("""
                UPDATE observation_generation_usages
                SET business_status = ?
                WHERE correlation_id = ?
                """, businessStatus, correlationId);
    }
}
