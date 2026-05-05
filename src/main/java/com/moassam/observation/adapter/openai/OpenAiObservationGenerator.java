package com.moassam.observation.adapter.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moassam.observation.application.required.ObservationGenerator;
import com.moassam.observation.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiObservationGenerator implements ObservationGenerator {

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;
    private final ObservationPromptBuilder observationPromptBuilder;

    @Value( "${openai.api-key}")
    private String apiKey;

    @Value( "${openai.model}")
    private String model;

    @Override
    public ObservationGeneration generateObservation(
            Age age,
            CurriculumType curriculumType,
            String situation,
            List<SectionType> sectionTypes,
            List<ObservationReference> observationReferences
    ) {
        JsonNode response = restClientBuilder
                .baseUrl("https://api.openai.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
                .post()
                .uri("/v1/responses")
                .body(buildRequest(age, curriculumType, situation, sectionTypes, observationReferences))
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("OpenAI 응답이 비어있습니다.");
        }

        return parse(response);
    }

    private Map<String, Object> buildRequest(
            Age age,
            CurriculumType curriculumType,
            String situation,
            List<SectionType> sectionTypes,
            List<ObservationReference> references
    ) {
        return Map.of(
                "model", model,
                "instructions", observationPromptBuilder.buildInstructions(),
                "input", observationPromptBuilder.buildInput(age, curriculumType, situation, sectionTypes, references),
                "max_output_tokens", 3000,
                "text", Map.of(
                        "format", Map.of(
                                "type", "json_schema",
                                "name", "observation_generation",
                                "strict", true,
                                "schema", buildSchema(sectionTypes)
                        )
                )
        );
    }

    private Map<String, Object> buildSchema(List<SectionType> sectionTypes) {
        List<String> sectionNames = sectionTypes.stream()
                .map(Enum::name)
                .toList();

        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "required", List.of("title", "summary", "sections"),
                "properties", Map.of(
                        "title", Map.of("type", "string"),
                        "summary", Map.of("type", "string"),
                        "sections", Map.of(
                                "type", "array",
                                "minItems", sectionNames.size(),
                                "maxItems", sectionNames.size(),
                                "items", Map.of(
                                        "type", "object",
                                        "additionalProperties", false,
                                        "required", List.of("sectionType", "content"),
                                        "properties", Map.of(
                                                "sectionType", Map.of(
                                                        "type", "string",
                                                        "enum", sectionNames
                                                ),
                                                "content", Map.of("type", "string")
                                        )
                                )
                        )
                )
        );
    }


    private ObservationGeneration parse(JsonNode response) {
        String outputText = response.path("output_text").asText(null);

        if (outputText == null) {
            for (JsonNode output : response.path("output")) {
                for (JsonNode content : output.path("content")) {
                    if (content.path("text").isTextual()) {
                        outputText = content.path("text").asText();
                        break;
                    }
                }
            }
        }

        if (outputText == null || outputText.isBlank()) {
            throw new IllegalStateException("OpenAI 응답에 output_text가 없습니다.");
        }

        try {
            return objectMapper.readValue(outputText, ObservationGeneration.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("OpenAI 관찰일지 생성 결과 파싱에 실패했습니다.", e);
        }
    }
}
