package com.moassam.observation.application;

import com.moassam.credit.application.provided.CreditUser;
import com.moassam.observation.application.required.ObservationRepository;
import com.moassam.observation.application.required.ObservationSectionRepository;
import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Observation;
import com.moassam.observation.domain.ObservationCreateRequest;
import com.moassam.observation.domain.ObservationGeneration;
import com.moassam.observation.domain.SectionType;
import com.moassam.user.application.UserActivityWriter;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ObservationPersistenceServiceTest {

    private final ObservationRepository observationRepository = mock(ObservationRepository.class);
    private final ObservationSectionRepository observationSectionRepository = mock(ObservationSectionRepository.class);
    private final CreditUser creditUser = mock(CreditUser.class);
    private final UserActivityWriter activityWriter = mock(UserActivityWriter.class);
    private final ObservationPersistenceService service = new ObservationPersistenceService(
            observationRepository,
            observationSectionRepository,
            creditUser,
            activityWriter
    );

    @Test
    void createRecordsAnalyticsInsideThePersistenceOperation() {
        Observation saved = observation(10L, 1L);
        given(observationRepository.save(org.mockito.ArgumentMatchers.any(Observation.class)))
                .willReturn(saved);

        Long observationId = service.create(1L, request(), generation());

        assertThat(observationId).isEqualTo(10L);
        then(activityWriter).should().recordObservationCreated(1L);
    }

    @Test
    void regenerateRecordsAnalyticsInsideThePersistenceOperation() {
        Observation observation = observation(10L, 1L);
        given(observationRepository.findByIdAndUserId(10L, 1L)).willReturn(Optional.of(observation));
        given(observationSectionRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

        service.regenerate(1L, 10L, 0L, generation());

        then(activityWriter).should().recordObservationRegenerated(1L);
    }

    private ObservationCreateRequest request() {
        return new ObservationCreateRequest(
                Age.AGE_5,
                List.of(SectionType.COMMUNICATION),
                "친구와 대화하며 놀았습니다."
        );
    }

    private ObservationGeneration generation() {
        return new ObservationGeneration(
                "생성 제목",
                "생성 총평",
                List.of(new ObservationGeneration.Section(SectionType.COMMUNICATION, "생성 내용"))
        );
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
}
