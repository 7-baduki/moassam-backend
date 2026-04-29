package com.moassam.observation.application.required;

import com.moassam.observation.application.command.ObservationGenerateCommand;
import com.moassam.observation.application.command.ObservationRegenerateCommand;
import com.moassam.observation.application.command.SectionRegenerateCommand;
import com.moassam.observation.application.result.ObservationGenerateResult;
import com.moassam.observation.application.result.ObservationResult;
import com.moassam.observation.application.result.ObservationSectionResult;
import com.moassam.observation.application.result.PhoneConsultationResult;

public interface ObservationGenerator {

    ObservationGenerateResult generate(ObservationGenerateCommand command);

    ObservationGenerateResult regenerate(ObservationRegenerateCommand command);

    ObservationSectionResult regenerateSection(SectionRegenerateCommand command);

    PhoneConsultationResult generatePhoneConsultation();
}
