package com.moassam.observation.application;

import com.moassam.observation.application.required.ObservationGenerator;
import com.moassam.observation.application.required.ObservationReferenceProvider;
import com.moassam.observation.application.required.ObservationRepository;
import com.moassam.observation.application.required.ObservationSectionRepository;
import com.moassam.observation.domain.*;
import com.moassam.observation.exception.ObservationErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class ObservationServiceTest {

    private final ObservationRepository observationRepository = mock(ObservationRepository.class);
    private final ObservationSectionRepository observationSectionRepository = mock(ObservationSectionRepository.class);
    private final ObservationReferenceProvider observationReferenceProvider = mock(ObservationReferenceProvider.class);
    private final ObservationGenerator observationGenerator = mock(ObservationGenerator.class);

    private final ObservationService observationService = new ObservationService(
            observationRepository,
            observationSectionRepository,
            observationReferenceProvider,
            observationGenerator
    );

    @Test
    void createObservation() {
        ObservationCreateRequest request = new ObservationCreateRequest(
                Age.AGE_5,
                List.of(SectionType.COMMUNICATION),
                "친구와 대화하며 놀았습니다."
        );

        given(observationReferenceProvider.getObservationReferences(any(), anyList()))
                .willReturn(List.of(reference(SectionType.COMMUNICATION)));

        given(observationGenerator.generateObservation(any(), any(), anyString(), anyList(), anyList()))
                .willReturn(generation());

        given(observationRepository.save(any(Observation.class)))
                .willAnswer(invocation -> {
                    Observation observation = invocation.getArgument(0);
                    ReflectionTestUtils.setField(observation, "id", 1L);
                    return observation;
                });

        Long observationId = observationService.createObservation(1L, request);

        assertThat(observationId).isEqualTo(1L);
        then(observationRepository).should().save(any(Observation.class));
        then(observationSectionRepository).should().saveAll(anyList());
    }

    @Test
    void createObservation_fail_ageRequired() {
        ObservationCreateRequest request = new ObservationCreateRequest(
                null,
                List.of(SectionType.COMMUNICATION),
                "관찰 내용"
        );

        assertThatThrownBy(() -> observationService.createObservation(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ObservationErrorCode.OBSERVATION_AGE_REQUIRED);
    }

    @Test
    void getObservation() {
        Observation observation = observation(1L, 1L);
        ObservationSection section = section(10L, 1L, SectionType.COMMUNICATION);

        given(observationRepository.findByIdAndUserId(1L, 1L))
                .willReturn(Optional.of(observation));
        given(observationSectionRepository.findAllByObservationIdOrderByDisplayOrderAsc(1L))
                .willReturn(List.of(section));

        ObservationDetail detail = observationService.getObservation(1L, 1L);

        assertThat(detail.observation()).isEqualTo(observation);
        assertThat(detail.sections()).containsExactly(section);
    }

    @Test
    void getObservation_notFound() {
        given(observationRepository.findByIdAndUserId(1L, 1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> observationService.getObservation(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ObservationErrorCode.OBSERVATION_NOT_FOUND);
    }

    @Test
    void regenerateObservation() {
        Observation observation = observation(1L, 1L);
        ObservationSection oldSection = section(10L, 1L, SectionType.COMMUNICATION);

        given(observationRepository.findByIdAndUserId(1L, 1L))
                .willReturn(Optional.of(observation));
        given(observationSectionRepository.findAllByObservationIdOrderByDisplayOrderAsc(1L))
                .willReturn(List.of(oldSection));
        given(observationReferenceProvider.getObservationReferences(any(), anyList()))
                .willReturn(List.of(reference(SectionType.COMMUNICATION)));
        given(observationGenerator.generateObservation(any(), any(), anyString(), anyList(), anyList()))
                .willReturn(generation());

        ObservationDetail detail = observationService.regenerateObservation(1L, 1L);

        assertThat(detail.observation().getTitle()).isEqualTo("생성 제목");
        assertThat(detail.sections()).hasSize(1);
        then(observationSectionRepository).should().deleteAllByObservationId(1L);
        then(observationSectionRepository).should().saveAll(anyList());
    }

    @Test
    void getObservationList() {
        Observation first = observation(3L, 1L);
        Observation second = observation(2L, 1L);

        given(observationRepository.findByUserIdOrderByIdDesc(eq(1L), any()))
                .willReturn(List.of(first, second));

        ObservationListDetail result = observationService.getObservationList(1L, null, 1);

        assertThat(result.observations()).containsExactly(first);
        assertThat(result.nextCursor()).isEqualTo(3L);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void deleteObservation() {
        Observation observation = observation(1L, 1L);

        given(observationRepository.findByIdAndUserId(1L, 1L))
                .willReturn(Optional.of(observation));

        observationService.deleteObservation(1L, 1L);

        then(observationSectionRepository).should().deleteAllByObservationId(1L);
        then(observationRepository).should().delete(observation);
    }


    private Observation observation(Long id, Long userId) {
        Observation observation = Observation.create(
                userId,
                "제목",
                "총평",
                Age.AGE_5,
                "친구와 놀았다."
        );
        ReflectionTestUtils.setField(observation, "id", id);
        return observation;
    }

    private ObservationSection section(Long id, Long observationId, SectionType sectionType) {
        ObservationSection section = ObservationSection.create(
                observationId,
                sectionType,
                "영역 내용"
        );
        ReflectionTestUtils.setField(section, "id", id);
        return section;
    }

    private ObservationReference reference(SectionType sectionType) {
        return new ObservationReference(
                CurriculumType.NURI,
                sectionType,
                "참고자료"
        );
    }

    private ObservationGeneration generation() {
        return new ObservationGeneration(
                "생성 제목",
                "생성 총평",
                List.of(new ObservationGeneration.Section(
                        SectionType.COMMUNICATION,
                        "생성 내용"
                ))
        );
    }
}
