package com.moassam.observation.adapter.integration.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moassam.observation.application.command.KeywordCommand;
import com.moassam.observation.application.command.ObservationGenerateCommand;
import com.moassam.observation.application.result.ObservationGenerateResult;
import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Curriculum;
import com.moassam.observation.domain.KeywordType;
import com.moassam.observation.domain.SectionType;
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
        ObservationGenerateCommand command = new ObservationGenerateCommand(
                "블록으로 집을 만들던 유아가 친구에게 지붕 모양을 설명하고, 무너진 부분을 함께 다시 쌓았다.",
                Age.AGE_5,
                Curriculum.NOORI,
                List.of(SectionType.SOCIAL, SectionType.COMMUNITY),
                List.of(
                        new KeywordCommand(KeywordType.ACTIVITY, "블록 놀이"),
                        new KeywordCommand(KeywordType.TRAIT, "협력적, 탐구적"),
                        new KeywordCommand(KeywordType.INTERACTION, "친구, 교사")
                )
        );

        ObservationGenerateResult result = generator.generate(command);

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
