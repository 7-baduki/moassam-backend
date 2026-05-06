package com.moassam.observation.adapter;

import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import com.moassam.observation.adapter.web.ObservationApi;
import com.moassam.observation.application.provided.*;
import com.moassam.observation.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ObservationApiTest extends RestDocsSupport {

    private final ObservationCreator observationCreator = mock(ObservationCreator.class);
    private final ObservationRegenerator observationRegenerator = mock(ObservationRegenerator.class);
    private final ObservationFinder observationFinder = mock(ObservationFinder.class);
    private final ObservationDeleter observationDeleter = mock(ObservationDeleter.class);

    @Override
    protected Object initController() {
        return new ObservationApi(observationCreator, observationRegenerator, observationFinder, observationDeleter);
    }

    @Test
    void createObservation() throws Exception {
        given(observationCreator.createObservation(any(), any()))
                .willReturn(1L);

        ObservationCreateRequest request = new ObservationCreateRequest(
                Age.AGE_5,
                List.of(SectionType.COMMUNICATION, SectionType.SOCIAL_RELATIONSHIP),
                "친구와 블록 놀이를 하며 자신의 생각을 말로 표현하였다."
        );

        mockMvc.perform(post("/api/v1/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("observation/create-observation",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        requestFields(
                                fieldWithPath("age").type(JsonFieldType.STRING).description("대상 연령: AGE_0 ~ AGE_5"),
                                fieldWithPath("sectionTypes").type(JsonFieldType.ARRAY).description("관찰 영역 목록"),
                                fieldWithPath("situation").type(JsonFieldType.STRING).description("교사가 입력한 관찰 내용")
                        ),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.observationId").type(JsonFieldType.NUMBER).description("관찰일지 ID")
                                )
                        )
                ));
    }

    @Test
    void regenerateObservation() throws Exception {
        Observation observation = observation(1L, 1L);
        List<ObservationSection> sections = List.of(section(10L, 1L, SectionType.COMMUNICATION));

        given(observationRegenerator.regenerateObservation(any(), eq(1L)))
                .willReturn(new ObservationDetail(observation, sections));

        mockMvc.perform(post("/api/v1/observations/{observationId}/regenerate", 1L))
                .andExpect(status().isOk())
                .andDo(document("observation/regenerate-observation",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.observationId").type(JsonFieldType.NUMBER).description("관찰일지 ID"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.summary").type(JsonFieldType.STRING).description("총평"),
                                        fieldWithPath("data.sections").type(JsonFieldType.ARRAY).description("영역별 관찰 내용"),
                                        fieldWithPath("data.sections[].sectionType").type(JsonFieldType.STRING).description("관찰 영역"),
                                        fieldWithPath("data.sections[].content").type(JsonFieldType.STRING).description("관찰일지 본문")
                                )
                        )
                ));
    }

    @Test
    void getObservation() throws Exception {
        Observation observation = observation(1L, 1L);
        List<ObservationSection> sections = List.of(section(10L, 1L, SectionType.COMMUNICATION));

        given(observationFinder.getObservation(any(), eq(1L)))
                .willReturn(new ObservationDetail(observation, sections));

        mockMvc.perform(get("/api/v1/observations/{observationId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("observation/get-observation",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.observationId").type(JsonFieldType.NUMBER).description("관찰일지 ID"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.summary").type(JsonFieldType.STRING).description("총평"),
                                        fieldWithPath("data.sections").type(JsonFieldType.ARRAY).description("영역별 관찰 내용"),
                                        fieldWithPath("data.sections[].sectionType").type(JsonFieldType.STRING).description("관찰 영역"),
                                        fieldWithPath("data.sections[].content").type(JsonFieldType.STRING).description("관찰일지 본문")
                                )
                        )
                ));
    }

    @Test
    void getObservationList() throws Exception {
        Observation observation = observation(1L, 1L);

        given(observationFinder.getObservationList(any(), isNull(), eq(20)))
                .willReturn(new ObservationListDetail(List.of(observation), null, false));

        mockMvc.perform(get("/api/v1/observations")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andDo(document("observation/getList-observation",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.items").type(JsonFieldType.ARRAY).description("관찰일지 목록"),
                                        fieldWithPath("data.items[].observationId").type(JsonFieldType.NUMBER).description("관찰일지 ID"),
                                        fieldWithPath("data.items[].title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.nextCursor").type(JsonFieldType.NULL).description("다음 커서"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                )
                        )
                ));
    }

    @Test
    void deleteObservation() throws Exception {
        willDoNothing().given(observationDeleter).deleteObservation(any(), eq(1L));

        mockMvc.perform(delete("/api/v1/observations/{observationId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("observation/delete-observation",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse()
                ));
    }

    private Observation observation(Long id, Long userId) {
        Observation observation = Observation.create(
                userId,
                "친구와 함께한 블록 놀이",
                "친구와 놀이하며 자신의 생각을 표현하는 모습이 관찰된다.",
                Age.AGE_5,
                "친구와 블록 놀이를 하였다."
        );
        ReflectionTestUtils.setField(observation, "id", id);
        return observation;
    }

    private ObservationSection section(Long id, Long observationId, SectionType sectionType) {
        ObservationSection section = ObservationSection.create(
                observationId,
                sectionType,
                "친구의 말을 듣고 자신의 생각을 말로 표현하는 모습이 관찰된다."
        );
        ReflectionTestUtils.setField(section, "id", id);
        return section;
    }
}
