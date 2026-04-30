package com.moassam.observation.adapter.integration.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moassam.observation.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("live-openai")
@SpringJUnitConfig
@Import({
        ObjectMapper.class,
        OpenAiObservationClient.class,
        OpenAiPromptTemplate.class,
        OpenAiObservationGenerator.class
})
@TestPropertySource(properties = {
        "openai.api-key=${OPENAI_API_KEY}",
        "openai.model=${OPENAI_MODEL:gpt-4o-mini}",
        "openai.base-url=${OPENAI_BASE_URL:https://api.openai.com}"
})
@EnabledIfSystemProperty(named = "liveOpenAi", matches = "true")
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiObservationGeneratorLiveTest {

    @Autowired
    private OpenAiObservationGenerator generator;

    @Test
    void generateObservationWithRealOpenAi() {
        ObservationGenerateInput input = new ObservationGenerateInput(
                "블록으로 집을 만들던 유아가 친구에게 지붕 모양을 설명하고, 무너진 부분을 함께 다시 쌓았다.",
                Age.AGE_5,
                Curriculum.NOORI,
                List.of(SectionType.SOCIAL, SectionType.COMMUNITY),
                List.of(
                        new KeywordInput(KeywordType.ACTIVITY, "블록 놀이"),
                        new KeywordInput(KeywordType.TRAIT, "협력적, 탐구적"),
                        new KeywordInput(KeywordType.INTERACTION, "친구, 교사")
                )
        );

        GeneratedObservationContent result = generator.generate(input);

        assertThat(result.sections())
                .hasSize(2)
                .extracting("type")
                .containsExactlyInAnyOrder(SectionType.SOCIAL, SectionType.COMMUNITY);
        assertThat(result.sections())
                .allSatisfy(section -> assertThat(section.getContent()).isNotBlank());
        assertThat(result.summaryContent()).isNotBlank();
        assertThat(result.phoneConsultationContent()).isNotBlank();

        result.sections().forEach(section ->
                log.info("sectionType: {}, section: {}", section.getType(), section.getContent())
        );
        log.info("summaryContent: {}", result.summaryContent());
        log.info("phoneConsultationContent: {}", result.phoneConsultationContent());
    }
}
