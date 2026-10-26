package com.moassam.observation.application;

import com.moassam.observation.application.provided.*;
import com.moassam.observation.application.required.ObservationGenerator;
import com.moassam.observation.application.required.ObservationReferenceProvider;
import com.moassam.observation.application.required.ObservationRepository;
import com.moassam.observation.application.required.ObservationSectionRepository;
import com.moassam.observation.domain.*;
import com.moassam.observation.exception.ObservationErrorCode;
import com.moassam.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObservationService implements ObservationCreator, ObservationRegenerator, ObservationFinder, ObservationDeleter, ObservationStatsFinder {

    private final ObservationRepository observationRepository;
    private final ObservationSectionRepository observationSectionRepository;
    private final ObservationReferenceProvider observationReferenceProvider;
    private final ObservationGenerator observationGenerator;
    private final ObservationPersistenceService observationPersistenceService;
    private final ObservationGenerationUsageRecorder usageRecorder;

    @Override
    public Long createObservation(Long userId, ObservationCreateRequest request) {
        validateCreateRequest(request);

        CurriculumType curriculumType = Observation.defineCurriculum(request.age());

        List<ObservationReference> references = observationReferenceProvider.getObservationReferences(curriculumType, request.sectionTypes());

        String correlationId = UUID.randomUUID().toString();
        ObservationGeneration result = generate(
                correlationId,
                userId,
                ObservationGenerationOperation.CREATE,
                request.age(),
                curriculumType,
                request.situation(),
                request.sectionTypes(),
                references
        );

        try {
            Long observationId = observationPersistenceService.create(userId, request, result);
            usageRecorder.markSaved(correlationId);
            return observationId;
        } catch (RuntimeException exception) {
            usageRecorder.markPersistenceFailed(correlationId);
            throw exception;
        }
    }

    @Override
    public ObservationDetail regenerateObservation(Long userId, Long observationId) {
        Observation observation = observationRepository.findByIdAndUserId(observationId, userId)
                .orElseThrow(() -> new BusinessException(ObservationErrorCode.OBSERVATION_NOT_FOUND));

        List<ObservationSection> currentSections =
                observationSectionRepository.findAllByObservationIdOrderByDisplayOrderAsc(observationId);

        List<SectionType> sectionTypes = currentSections.stream()
                .map(ObservationSection::getSectionType)
                .toList();

        List<ObservationReference> references =
                observationReferenceProvider.getObservationReferences(
                        observation.getCurriculumType(),
                        sectionTypes
                );

        String correlationId = UUID.randomUUID().toString();
        ObservationGeneration result = generate(
                correlationId,
                userId,
                ObservationGenerationOperation.REGENERATE,
                observation.getAge(),
                observation.getCurriculumType(),
                observation.getSituation(),
                sectionTypes,
                references
        );

        try {
            ObservationDetail detail = observationPersistenceService.regenerate(
                    userId,
                    observationId,
                    observation.getVersion(),
                    result
            );
            usageRecorder.markSaved(correlationId);
            return detail;
        } catch (RuntimeException exception) {
            usageRecorder.markPersistenceFailed(correlationId);
            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ObservationDetail getObservation(Long userId, Long observationId) {
        Observation observation = observationRepository.findByIdAndUserId(observationId, userId)
                .orElseThrow(() -> new BusinessException(ObservationErrorCode.OBSERVATION_NOT_FOUND));

        List<ObservationSection> sections = observationSectionRepository.findAllByObservationIdOrderByDisplayOrderAsc(observationId);

        return new ObservationDetail(observation, sections);
    }

    @Override
    @Transactional(readOnly = true)
    public ObservationListDetail getObservationList(Long userId, Long cursor, int size) {
        int pageSize = Math.clamp(size, 1, 50);
        Pageable pageable = PageRequest.of(0, pageSize + 1);

        List<Observation> observations = cursor == null
                ? observationRepository.findByUserIdOrderByIdDesc(userId, pageable)
                : observationRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, cursor, pageable);

        boolean hasNext = observations.size() > pageSize;

        List<Observation> items = hasNext
                ? observations.subList(0, pageSize)
                : observations;

        Long nextCursor = hasNext
                ? items.get(items.size() - 1).getId()
                : null;

        return new ObservationListDetail(items, nextCursor, hasNext);
    }

    @Override
    @Transactional
    public void deleteObservation(Long userId, Long observationId) {
        Observation observation = observationRepository.findByIdAndUserId(observationId, userId)
                .orElseThrow(() -> new BusinessException(ObservationErrorCode.OBSERVATION_NOT_FOUND));

        observationSectionRepository.deleteAllByObservationId(observationId);
        observationRepository.delete(observation);
    }

    @Override
    public long countByUserId(Long userId) {
        return observationRepository.countByUserId(userId);
    }

    private ObservationGeneration generate(
            String correlationId,
            Long userId,
            ObservationGenerationOperation operation,
            Age age,
            CurriculumType curriculumType,
            String situation,
            List<SectionType> sectionTypes,
            List<ObservationReference> references
    ) {
        long startedAt = System.nanoTime();
        try {
            ObservationGeneration generation = observationGenerator.generateObservation(
                    age,
                    curriculumType,
                    situation,
                    sectionTypes,
                    references
            );
            usageRecorder.recordProviderSuccess(
                    correlationId,
                    userId,
                    operation,
                    generation,
                    elapsedMillis(startedAt)
            );
            return generation;
        } catch (RuntimeException exception) {
            usageRecorder.recordProviderFailure(
                    correlationId,
                    userId,
                    operation,
                    elapsedMillis(startedAt)
            );
            throw exception;
        }
    }

    private long elapsedMillis(long startedAt) {
        return Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
    }

    private void validateCreateRequest(ObservationCreateRequest request) {
        if (request.age() == null) {
            throw new BusinessException(ObservationErrorCode.OBSERVATION_AGE_REQUIRED);
        }

        if (request.sectionTypes() == null || request.sectionTypes().isEmpty()) {
            throw new BusinessException(ObservationErrorCode.OBSERVATION_SECTION_REQUIRED);
        }

        if (request.situation() == null || request.situation().isBlank()) {
            throw new BusinessException(ObservationErrorCode.OBSERVATION_SITUATION_REQUIRED);
        }
    }
}
