package com.moassam.observation.application.required;

import com.moassam.observation.domain.*;

public interface ObservationGenerator {

    GeneratedObservationContent generate(ObservationGenerateInput input);

    GeneratedObservationContent regenerate(ObservationRegenerateInput input);

    ObservationSection regenerateSection(SectionRegenerateInput input);

    GeneratedObservationContent generatePhoneConsultation();
}
