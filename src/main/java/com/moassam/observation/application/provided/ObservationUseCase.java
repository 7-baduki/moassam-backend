package com.moassam.observation.application.provided;

import com.moassam.observation.application.command.ObservationGenerateCommand;
import com.moassam.observation.application.command.ObservationRegenerateCommand;
import com.moassam.observation.application.command.SectionRegenerateCommand;
import com.moassam.observation.application.command.SectionUpdateCommand;
import com.moassam.observation.application.result.ObservationResult;
import com.moassam.observation.application.result.ObservationSectionResult;
import com.moassam.observation.application.result.PhoneConsultationResult;

public interface ObservationUseCase {

    ObservationResult generateObservation(Long userId, ObservationGenerateCommand command);

    ObservationResult get(Long userId, Long observationId);

    ObservationSectionResult getSection(Long userId, Long observationId, Long sectionId);

    ObservationResult regenerate(Long userId, Long observationId, ObservationRegenerateCommand command);

    ObservationSectionResult regenerateSection(
            Long userId,
            Long observationId,
            Long sectionId,
            SectionRegenerateCommand command
    );

    ObservationSectionResult updateSection(
            Long userId,
            Long observationId,
            Long sectionId,
            SectionUpdateCommand command
    );

    PhoneConsultationResult createPhoneConsultation(Long userId, Long observationId);

    void save(Long userId, Long observationId);
}
