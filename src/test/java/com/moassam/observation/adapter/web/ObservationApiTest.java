package com.moassam.observation.adapter.web;

import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import com.moassam.observation.application.command.ObservationGenerateCommand;
import com.moassam.observation.application.provided.ObservationUseCase;
import com.moassam.observation.application.result.KeywordResult;
import com.moassam.observation.application.result.ObservationResult;
import com.moassam.observation.application.result.ObservationSectionResult;
import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Curriculum;
import com.moassam.observation.domain.KeywordType;
import com.moassam.observation.domain.ObservationStatus;
import com.moassam.observation.domain.SectionType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ObservationApiTest extends RestDocsSupport {

    private final ObservationUseCase observationUseCase = mock(ObservationUseCase.class);

    @Override
    protected Object initController() {
        return new ObservationApi(observationUseCase);
    }

    @Test
    void generateObservation() throws Exception {
        given(observationUseCase.generateObservation(
                nullable(Long.class),
                any(ObservationGenerateCommand.class)
        )).willReturn(observationResult());

        Map<String, Object> request = Map.of(
                "memo", "블록으로 집을 만들고 친구에게 설명함",
                "age", "AGE_5",
                "curriculum", "NOORI",
                "sectionTypes", List.of("SOCIAL", "SCIENCE"),
                "keywords", List.of(
                        Map.of("type", "ACTIVITY", "value", "자유놀이"),
                        Map.of("type", "TRAIT", "value", "탐구적")
                )
        );

        mockMvc.perform(post("/api/v1/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("observation/generate",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestFields(
                                fieldWithPath("memo").type(JsonFieldType.STRING).description("Observation memo"),
                                fieldWithPath("age").type(JsonFieldType.STRING).description("Target age"),
                                fieldWithPath("curriculum").type(JsonFieldType.STRING).description("Curriculum"),
                                fieldWithPath("sectionTypes[]").type(JsonFieldType.ARRAY).description("Curriculum section types"),
                                fieldWithPath("keywords[]").type(JsonFieldType.ARRAY).description("Keywords"),
                                fieldWithPath("keywords[].type").type(JsonFieldType.STRING).description("Keyword type"),
                                fieldWithPath("keywords[].value").type(JsonFieldType.STRING).description("Keyword value")
                        ),
                        responseFields(CommonDocumentation.successResponseFields(
                                fieldWithPath("data.observationId").type(JsonFieldType.NUMBER).description("Observation ID"),
                                fieldWithPath("data.memo").type(JsonFieldType.STRING).description("Observation memo"),
                                fieldWithPath("data.age").type(JsonFieldType.STRING).description("Target age"),
                                fieldWithPath("data.curriculum").type(JsonFieldType.STRING).description("Curriculum"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("Observation status"),
                                fieldWithPath("data.keywords[]").type(JsonFieldType.ARRAY).description("Keywords"),
                                fieldWithPath("data.keywords[].type").type(JsonFieldType.STRING).description("Keyword type"),
                                fieldWithPath("data.keywords[].value").type(JsonFieldType.STRING).description("Keyword value"),
                                fieldWithPath("data.sections[]").type(JsonFieldType.ARRAY).description("Observation sections"),
                                fieldWithPath("data.sections[].sectionId").type(JsonFieldType.NUMBER).description("Section ID"),
                                fieldWithPath("data.sections[].type").type(JsonFieldType.STRING).description("Section type"),
                                fieldWithPath("data.sections[].content").type(JsonFieldType.STRING).description("Section content"),
                                fieldWithPath("data.sections[].edited").type(JsonFieldType.BOOLEAN).description("Whether section is edited"),
                                fieldWithPath("data.summaryContent").type(JsonFieldType.STRING).description("Summary content"),
                                fieldWithPath("data.phoneConsultationContent").type(JsonFieldType.STRING).description("Phone consultation content"),
                                fieldWithPath("data.derivedContentStale").type(JsonFieldType.BOOLEAN).description("Whether derived content is stale"),
                                fieldWithPath("data.savedAt").type(JsonFieldType.STRING).description("Saved date time")
                        ))
                ));
    }

    private ObservationResult observationResult() {
        return new ObservationResult(
                1L,
                "블록으로 집을 만들고 친구에게 설명함",
                Age.AGE_5,
                Curriculum.NOORI,
                ObservationStatus.TEMP,
                List.of(
                        new KeywordResult(KeywordType.ACTIVITY, "자유놀이"),
                        new KeywordResult(KeywordType.TRAIT, "탐구적")
                ),
                List.of(new ObservationSectionResult(
                        10L,
                        SectionType.SOCIAL,
                        "친구에게 자신이 구성한 블록 집의 특징을 설명하였다.",
                        false
                )),
                "또래와 상호작용하며 자신의 생각을 표현하는 모습이 관찰되었다.",
                "오늘 블록 놀이 중 친구에게 자신의 구성물을 설명하는 모습이 있었습니다.",
                false,
                LocalDateTime.of(2026, 4, 29, 12, 0)
        );
    }
}
