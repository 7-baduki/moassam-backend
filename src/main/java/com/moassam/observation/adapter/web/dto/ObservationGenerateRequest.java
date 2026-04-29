package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.application.command.ObservationGenerateCommand;
import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Curriculum;
import com.moassam.observation.domain.SectionType;

import java.util.List;

public record ObservationGenerateRequest(
        String memo,
        Age age,
        Curriculum curriculum,
        List<SectionType> sectionTypes,
        List<KeywordRequest> keywords
) {

    public ObservationGenerateCommand toCommand() {
        return new ObservationGenerateCommand(
                memo,
                age,
                curriculum,
                sectionTypes,
                keywords == null
                        ? List.of()
                        : keywords.stream()
                                .map(KeywordRequest::toCommand)
                                .toList()
        );
    }
}
