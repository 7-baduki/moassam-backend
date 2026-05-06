package com.moassam.observation.application.required;

import com.moassam.observation.domain.*;

import java.util.List;

public interface ObservationGenerator {
    ObservationGeneration generateObservation(
            Age age,
            CurriculumType curriculumType,
            String situation,
            List<SectionType> sectionTypes,
            List<ObservationReference> observationReferences
    );
}
