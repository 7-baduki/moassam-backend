package com.moassam.observation.application.required;

import com.moassam.observation.domain.ObservationGenerationUsage;

public interface ObservationGenerationUsageRepository {

    void insert(ObservationGenerationUsage usage);

    void updateBusinessStatus(String correlationId, String businessStatus);
}
