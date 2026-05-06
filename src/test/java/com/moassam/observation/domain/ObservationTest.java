package com.moassam.observation.domain;

import com.moassam.observation.exception.ObservationErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ObservationTest {

    @Test
    void createObservation_standartCurriculum() {
        Observation observation = Observation.create(
                1L,
                "관찰 제목",
                "총평",
                Age.AGE_1,
                "관찰 내용"
        );

        assertThat(observation.getUserId()).isEqualTo(1L);
        assertThat(observation.getTitle()).isEqualTo("관찰 제목");
        assertThat(observation.getSummary()).isEqualTo("총평");
        assertThat(observation.getAge()).isEqualTo(Age.AGE_1);
        assertThat(observation.getCurriculumType()).isEqualTo(CurriculumType.STANDARD);
        assertThat(observation.getSituation()).isEqualTo("관찰 내용");

    }

    @Test
    void createObservation_nuriCurriculum() {
        Observation observation = Observation.create(
                1L,
                "관찰 제목",
                "총평",
                Age.AGE_4,
                "관찰 내용"
        );
        assertThat(observation.getAge()).isEqualTo(Age.AGE_4);
        assertThat(observation.getCurriculumType()).isEqualTo(CurriculumType.NURI);
    }

    @Test
    void createObservation_ageRequired() {
        assertThatThrownBy(() -> Observation.create(
                1L,
                "관찰 제목",
                "관찰 요약",
                null,
                "관찰 내용"
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ObservationErrorCode.OBSERVATION_AGE_REQUIRED);
    }

    @Test
    void updateGeneratedResult() {
        Observation observation = Observation.create(
                1L,
                "기존 제목",
                "기존 요약",
                Age.AGE_5,
                "관찰 내용"
        );

        observation.updateGeneratedResult("새 제목", "새 요약");

        assertThat(observation.getTitle()).isEqualTo("새 제목");
        assertThat(observation.getSummary()).isEqualTo("새 요약");
    }

    @Test
    void defineCurriculum() {
        assertThat(Observation.defineCurriculum(Age.AGE_0)).isEqualTo(CurriculumType.STANDARD);
        assertThat(Observation.defineCurriculum(Age.AGE_1)).isEqualTo(CurriculumType.STANDARD);
        assertThat(Observation.defineCurriculum(Age.AGE_2)).isEqualTo(CurriculumType.STANDARD);
        assertThat(Observation.defineCurriculum(Age.AGE_3)).isEqualTo(CurriculumType.NURI);
        assertThat(Observation.defineCurriculum(Age.AGE_4)).isEqualTo(CurriculumType.NURI);
        assertThat(Observation.defineCurriculum(Age.AGE_5)).isEqualTo(CurriculumType.NURI);
    }

}
