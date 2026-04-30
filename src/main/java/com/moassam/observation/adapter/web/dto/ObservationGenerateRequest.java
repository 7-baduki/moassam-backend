package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Curriculum;
import com.moassam.observation.domain.ObservationGenerateInput;
import com.moassam.observation.domain.SectionType;

import java.util.List;

public record ObservationGenerateRequest(
        String memo,
        Age age,
        Curriculum curriculum,
        List<SectionType> sectionTypes,
        List<KeywordRequest> keywords
) {

    public ObservationGenerateInput toInput() {
        return new ObservationGenerateInput(
                memo,
                age,
                curriculum,
                sectionTypes,
                keywords == null
                        ? List.of()
                        : keywords.stream()
                                .map(KeywordRequest::toInput)
                                .toList()
        );
    }
}
