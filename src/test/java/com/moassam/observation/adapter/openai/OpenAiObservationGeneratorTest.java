package com.moassam.observation.adapter.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moassam.observation.domain.ObservationGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class OpenAiObservationGeneratorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OpenAiObservationGenerator generator = new OpenAiObservationGenerator(
            mock(RestClient.Builder.class),
            objectMapper,
            new ObservationPromptBuilder()
    );

    @Test
    void parse_includesNumericTokenUsageWithoutPromptOrOutputContentMetadata() throws Exception {
        ReflectionTestUtils.setField(generator, "model", "gpt-test");
        String generated = """
                {"title":"제목","summary":"요약","sections":[
                  {"sectionType":"COMMUNICATION","content":"내용"}
                ]}
                """;
        JsonNode response = objectMapper.readTree("""
                {
                  "id":"resp_123",
                  "model":"gpt-5-mini",
                  "output_text":%s,
                  "usage":{"input_tokens":101,"output_tokens":55,"total_tokens":156}
                }
                """.formatted(objectMapper.writeValueAsString(generated)));

        ObservationGeneration result = ReflectionTestUtils.invokeMethod(generator, "parse", response);

        assertThat(result).isNotNull();
        assertThat(result.providerResponseId()).isEqualTo("resp_123");
        assertThat(result.model()).isEqualTo("gpt-5-mini");
        assertThat(result.inputTokens()).isEqualTo(101L);
        assertThat(result.outputTokens()).isEqualTo(55L);
        assertThat(result.totalTokens()).isEqualTo(156L);
    }
}
