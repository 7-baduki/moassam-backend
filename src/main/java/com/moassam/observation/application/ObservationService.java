package com.moassam.observation.application;

import com.moassam.observation.application.provided.*;
import com.moassam.observation.application.required.ObservationGenerator;
import com.moassam.observation.application.required.ObservationRepository;
import com.moassam.observation.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ObservationService implements
        ObservationCreator,
        ObservationFinder,
        ObservationRegenerator,
        ObservationSectionModifier,
        PhoneConsultationCreator,
        ObservationSaver
{

    private final ObservationRepository observationRepository;
    private final ObservationGenerator observationGenerator;

    @Transactional
    @Override
    public Observation generateObservation(
            Long userId,
            ObservationGenerateInput input
    ) {
        Observation observation = Observation.create(
                userId,
                input.memo(),
                input.age(),
                input.curriculum(),
                input.keywords().stream()
                        .map(keyword -> Keyword.create(
                                keyword.type(),
                                keyword.value()
                        ))
                        .toList()
        );

        GeneratedObservationContent generated = observationGenerator.generate(input);

        observation.replaceGeneratedContent(
                generated.sections(),
                generated.summaryContent(),
                generated.phoneConsultationContent()
        );

        return observationRepository.save(observation);
    }

    @Override
    public Observation get(Long userId, Long observationId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ObservationSection getSection(Long userId, Long observationId, Long sectionId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Observation regenerate(
            Long userId,
            Long observationId,
            ObservationRegenerateInput input
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ObservationSection regenerateSection(
            Long userId,
            Long observationId,
            Long sectionId,
            SectionRegenerateInput input
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ObservationSection updateSection(
            Long userId,
            Long observationId,
            Long sectionId,
            SectionUpdateInput input
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Observation createPhoneConsultation(Long userId, Long observationId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void save(Long userId, Long observationId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}


