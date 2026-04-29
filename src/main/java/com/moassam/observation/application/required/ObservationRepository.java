package com.moassam.observation.application.required;

import com.moassam.observation.domain.Observation;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ObservationRepository extends Repository<Observation, Long> {

    Observation save(Observation observation);

    Optional<Observation> findById(Long id);
}
