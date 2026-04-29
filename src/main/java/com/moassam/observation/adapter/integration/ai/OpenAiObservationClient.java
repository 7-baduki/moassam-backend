package com.moassam.observation.adapter.integration.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moassam.observation.adapter.integration.ai.dto.OpenAiObservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiObservationClient {

    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.base-url}")
    private String baseUrl;

    public OpenAiObservationResponse generate(
            String systemPrompt,
            String userPrompt
    ) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        Map<String, Object> request = Map.of(
                "model", model,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", systemPrompt
                        ),
                        Map.of(
                                "role", "user",
                                "content", userPrompt
                        )
                )
        );

        OpenAiChatCompletionResponse response = restClient.post()
                .uri("/v1/chat/completions")
                .body(request)
                .retrieve()
                .body(OpenAiChatCompletionResponse.class);

        String content = response.choices().getFirst().message().content();

        try {
            return objectMapper.readValue(content, OpenAiObservationResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse OpenAI observation response.", e);
        }
    }

    private record OpenAiChatCompletionResponse(
            List<Choice> choices
    ) {
    }

    private record Choice(
            Message message
    ) {
    }

    private record Message(
            String content
    ) {
    }
}
