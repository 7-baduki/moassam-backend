package com.moassam.observation.application.required;

import com.moassam.observation.domain.ObservationSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservationSectionRepository extends JpaRepository<ObservationSection, Long> {
    List<ObservationSection> findAllByObservationIdOrderByDisplayOrderAsc(Long observationId);

    void deleteAllByObservationId(Long observationId);

    void deleteByObservationIdIn(List<Long> observationIds);
}
