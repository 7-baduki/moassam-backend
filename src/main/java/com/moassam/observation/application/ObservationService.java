package com.moassam.observation.application;

import com.moassam.observation.application.provided.ObservationCreator;
import com.moassam.observation.application.provided.ObservationDeleter;
import com.moassam.observation.application.provided.ObservationFinder;
import com.moassam.observation.application.provided.ObservationRegenerator;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObservationService implements ObservationCreator, ObservationRegenerator, ObservationFinder, ObservationDeleter {

    private final ObservationRepository observationRepository;
    private final ObservationSectionRepository observationSectionRepository;
    private final ObservationReferenceProvider observationReferenceProvider;
    private final ObservationGenerator observationGenerator;

    @Override
    @Transactional
    public Long createObservation(Long userId, ObservationCreateRequest request) {
        validateCreateRequest(request);

        CurriculumType curriculumType = Observation.defineCurriculum(request.age());

        List<ObservationReference> references = observationReferenceProvider.getObservationReferences(curriculumType, request.sectionTypes());

        ObservationGeneration result = observationGenerator.generateObservation(
                request.age(),
                curriculumType,
                request.situation(),
                request.sectionTypes(),
                references
        );

        Observation observation = Observation.create(
                userId,
                result.title(),
                result.summary(),
                request.age(),
                request.situation()
        );

        Observation saved = observationRepository.save(observation);

        List<ObservationSection> sections = result.sections().stream()
                .map(section -> ObservationSection.create(
                        saved.getId(),
                        section.sectionType(),
                        section.content()
                ))
                .toList();
        observationSectionRepository.saveAll(sections);

        return saved.getId();
    }

    @Override
    @Transactional
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

        ObservationGeneration result = observationGenerator.generateObservation(
                observation.getAge(),
                observation.getCurriculumType(),
                observation.getSituation(),
                sectionTypes,
                references
        );

        observation.updateGeneratedResult(result.title(), result.summary());

        observationSectionRepository.deleteAllByObservationId(observationId);

        List<ObservationSection> newSections = result.sections().stream()
                .map(section -> ObservationSection.create(
                        observationId,
                        section.sectionType(),
                        section.content()
                ))
                .toList();

        observationSectionRepository.saveAll(newSections);

        return new ObservationDetail(observation, newSections);
    }

    @Override
    public ObservationDetail getObservation(Long userId, Long observationId) {
        Observation observation = observationRepository.findByIdAndUserId(observationId, userId)
                .orElseThrow(() -> new BusinessException(ObservationErrorCode.OBSERVATION_NOT_FOUND));

        List<ObservationSection> sections = observationSectionRepository.findAllByObservationIdOrderByDisplayOrderAsc(observationId);

        return new ObservationDetail(observation, sections);
    }

    @Override
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
