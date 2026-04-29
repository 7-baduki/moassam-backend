package com.moassam.observation.adapter.integration.ai;

import com.moassam.observation.adapter.integration.ai.dto.OpenAiObservationResponse;
import com.moassam.observation.application.command.ObservationGenerateCommand;
import com.moassam.observation.application.command.ObservationRegenerateCommand;
import com.moassam.observation.application.command.SectionRegenerateCommand;
import com.moassam.observation.application.required.ObservationGenerator;
import com.moassam.observation.application.result.ObservationGenerateResult;
import com.moassam.observation.application.result.ObservationResult;
import com.moassam.observation.application.result.ObservationSectionResult;
import com.moassam.observation.application.result.PhoneConsultationResult;
import com.moassam.observation.domain.ObservationSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAiObservationGenerator implements ObservationGenerator {

    private final OpenAiPromptTemplate promptTemplate;
    private final OpenAiObservationClient openAiObservationClient;

    @Override
    public ObservationGenerateResult generate(ObservationGenerateCommand command) {
        OpenAiObservationResponse response = openAiObservationClient.generate(
                promptTemplate.systemBase(),
                promptTemplate.observationBase(command)
        );

        return new ObservationGenerateResult(
                response.sections().stream()
                        .map(section -> ObservationSection.create(
                                section.type(),
                                section.content()
                        ))
                        .toList(),
                response.summaryContent(),
                response.phoneConsultationContent()
        );
    }

    @Override
    public ObservationGenerateResult regenerate(ObservationRegenerateCommand command) {
        return null;
    }

    @Override
    public ObservationSectionResult regenerateSection(SectionRegenerateCommand command) {
        return null;
    }

    @Override
    public PhoneConsultationResult generatePhoneConsultation() {
        return null;
    }
}
