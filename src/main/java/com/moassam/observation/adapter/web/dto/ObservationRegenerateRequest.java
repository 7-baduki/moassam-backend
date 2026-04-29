package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.application.command.ObservationRegenerateCommand;
import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Curriculum;
import com.moassam.observation.domain.SectionType;

import java.util.List;

public record ObservationRegenerateRequest(
        String memo,
        Age age,
        Curriculum curriculum,
        List<SectionType> sectionTypes,
        List<KeywordRequest> keywords
) {

    public ObservationRegenerateCommand toCommand() {
        return new ObservationRegenerateCommand(
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
