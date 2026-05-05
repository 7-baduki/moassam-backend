package com.moassam.observation.domain;

import com.moassam.observation.exception.ObservationErrorCode;
import com.moassam.shared.domain.BaseEntity;
import com.moassam.shared.exception.BusinessException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Observation extends BaseEntity {

    private Long id;
    private Long userId;
    private String title;
    private String summary;
    private Age age;
    private CurriculumType curriculumType;
    private String situation;

    public static Observation create(
            Long userId,
            String title,
            String summary,
            Age age,
            String situation
    ) {
        Observation observation = new Observation();

        observation.userId = userId;
        observation.title = title;
        observation.summary = summary;
        observation.age = age;
        observation.curriculumType = defineCurriculum(age);
        observation.situation = situation;

        return observation;
    }

    public void updateGeneratedResult(String title, String summary) {
        this.title = title;
        this.summary = summary;
    }

    public static CurriculumType defineCurriculum(Age age) {
        if (age == null) {
            throw new BusinessException(ObservationErrorCode.OBSERVATION_AGE_REQUIRED);
        }

        if (age == Age.AGE_0 || age == Age.AGE_1 || age == Age.AGE_2) {
            return CurriculumType.STANDARD;
        } else
            return CurriculumType.NURI;

    }


}