package com.moassam.observation.application.required;

import com.moassam.observation.domain.Observation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
    Optional<Observation> findByIdAndUserId(Long id, Long userId);

    List<Observation> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    List<Observation> findByUserIdAndIdLessThanOrderByIdDesc(
            Long userId,
            Long cursor,
            Pageable pageable
    );

}
