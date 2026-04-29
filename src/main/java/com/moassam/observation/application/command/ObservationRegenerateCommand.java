package com.moassam.observation.application.command;

import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.Curriculum;
import com.moassam.observation.domain.SectionType;

import java.util.List;

public record ObservationRegenerateCommand(
        String memo,
        Age age,
        Curriculum curriculum,
        List<SectionType> sectionTypes,
        List<KeywordCommand> keywords
) {
}
