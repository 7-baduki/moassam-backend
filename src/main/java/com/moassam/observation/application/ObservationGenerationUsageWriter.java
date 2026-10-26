package com.moassam.observation.application;

import com.moassam.observation.application.required.ObservationGenerationUsageRepository;
import com.moassam.observation.domain.ObservationGenerationUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ObservationGenerationUsageWriter {

    private final ObservationGenerationUsageRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insert(ObservationGenerationUsage usage) {
        repository.insert(usage);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateBusinessStatus(String correlationId, String businessStatus) {
        repository.updateBusinessStatus(correlationId, businessStatus);
    }
}
