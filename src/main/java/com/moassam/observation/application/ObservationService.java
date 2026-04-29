package com.moassam.observation.application;

import com.moassam.observation.application.command.ObservationGenerateCommand;
import com.moassam.observation.application.command.ObservationRegenerateCommand;
import com.moassam.observation.application.command.SectionRegenerateCommand;
import com.moassam.observation.application.command.SectionUpdateCommand;
import com.moassam.observation.application.provided.ObservationUseCase;
import com.moassam.observation.application.required.ObservationGenerator;
import com.moassam.observation.application.required.ObservationRepository;
import com.moassam.observation.application.result.ObservationGenerateResult;
import com.moassam.observation.application.result.ObservationResult;
import com.moassam.observation.application.result.ObservationSectionResult;
import com.moassam.observation.application.result.PhoneConsultationResult;
import com.moassam.observation.domain.Keyword;
import com.moassam.observation.domain.Observation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ObservationService implements ObservationUseCase {

    private final ObservationRepository observationRepository;
    private final ObservationGenerator observationGenerator;

    @Transactional
    @Override
    public ObservationResult generateObservation(
            Long userId,
            ObservationGenerateCommand command
    ) {
        Observation observation = Observation.create(
                userId,
                command.memo(),
                command.age(),
                command.curriculum(),
                command.keywords().stream()
                        .map(keyword -> Keyword.create(
                                keyword.type(),
                                keyword.value()
                        ))
                        .toList()
        );

        ObservationGenerateResult generateResult = observationGenerator.generate(command);

        observation.replaceGeneratedContent(
                generateResult.sections(),
                generateResult.summaryContent(),
                generateResult.phoneConsultationContent()
        );

        Observation savedObservation = observationRepository.save(observation);

        return ObservationResult.from(savedObservation);
    }

    @Override
    public ObservationResult get(Long userId, Long observationId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ObservationSectionResult getSection(Long userId, Long observationId, Long sectionId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ObservationResult regenerate(Long userId, Long observationId, ObservationRegenerateCommand command) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ObservationSectionResult regenerateSection(
            Long userId,
            Long observationId,
            Long sectionId,
            SectionRegenerateCommand command
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ObservationSectionResult updateSection(
            Long userId,
            Long observationId,
            Long sectionId,
            SectionUpdateCommand command
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public PhoneConsultationResult createPhoneConsultation(Long userId, Long observationId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void save(Long userId, Long observationId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}


