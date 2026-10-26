package com.moassam.observation.application;

import com.moassam.credit.application.provided.CreditUser;
import com.moassam.observation.application.required.ObservationRepository;
import com.moassam.observation.application.required.ObservationSectionRepository;
import com.moassam.observation.domain.Observation;
import com.moassam.observation.domain.ObservationCreateRequest;
import com.moassam.observation.domain.ObservationDetail;
import com.moassam.observation.domain.ObservationGeneration;
import com.moassam.observation.domain.ObservationSection;
import com.moassam.observation.exception.ObservationErrorCode;
import com.moassam.shared.exception.BusinessException;
import com.moassam.user.application.UserActivityWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ObservationPersistenceService {

    private final ObservationRepository observationRepository;
    private final ObservationSectionRepository observationSectionRepository;
    private final CreditUser creditUser;
    private final UserActivityWriter activityWriter;

    @Transactional
    public Long create(Long userId, ObservationCreateRequest request, ObservationGeneration generation) {
        Observation observation = Observation.create(
                userId,
                generation.title(),
                generation.summary(),
                request.age(),
                request.situation()
        );
        Observation saved = observationRepository.save(observation);

        creditUser.useForCreateObservation(userId, saved.getId());
        observationSectionRepository.saveAll(toSections(saved.getId(), generation));
        activityWriter.recordObservationCreated(userId);
        return saved.getId();
    }

    @Transactional
    public ObservationDetail regenerate(
            Long userId,
            Long observationId,
            long expectedVersion,
            ObservationGeneration generation
    ) {
        Observation observation = observationRepository.findByIdAndUserId(observationId, userId)
                .orElseThrow(() -> new BusinessException(ObservationErrorCode.OBSERVATION_NOT_FOUND));
        if (observation.getVersion() != expectedVersion) {
            throw new ObjectOptimisticLockingFailureException(Observation.class, observationId);
        }

        observation.updateGeneratedResult(generation.title(), generation.summary());
        observationSectionRepository.deleteAllByObservationId(observationId);
        List<ObservationSection> sections = toSections(observationId, generation);
        creditUser.useForRegenerateObservation(userId, observationId);
        observationSectionRepository.saveAll(sections);
        activityWriter.recordObservationRegenerated(userId);
        return new ObservationDetail(observation, sections);
    }

    private List<ObservationSection> toSections(Long observationId, ObservationGeneration generation) {
        return generation.sections().stream()
                .map(section -> ObservationSection.create(
                        observationId,
                        section.sectionType(),
                        section.content()
                ))
                .toList();
    }
}
