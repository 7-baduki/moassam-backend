package com.moassam.observation.adapter.integration.ai;

import com.moassam.observation.adapter.integration.ai.dto.OpenAiObservationResponse;
import com.moassam.observation.application.required.ObservationGenerator;
import com.moassam.observation.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAiObservationGenerator implements ObservationGenerator {

    private final OpenAiPromptTemplate promptTemplate;
    private final OpenAiObservationClient openAiObservationClient;

    @Override
    public GeneratedObservationContent generate(ObservationGenerateInput input) {
        OpenAiObservationResponse response = openAiObservationClient.generate(
                promptTemplate.systemBase(),
                promptTemplate.observationBase(input)
        );

        return new GeneratedObservationContent(
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
    public GeneratedObservationContent regenerate(ObservationRegenerateInput input) {
        return null;
    }

    @Override
    public ObservationSection regenerateSection(SectionRegenerateInput input) {
        return null;
    }

    @Override
    public GeneratedObservationContent generatePhoneConsultation() {
        return null;
    }
}
