package com.moassam.observation.application.required;

import com.moassam.observation.domain.CurriculumType;
import com.moassam.observation.domain.ObservationReference;
import com.moassam.observation.domain.SectionType;

import java.util.List;

public interface ObservationReferenceProvider {
    List<ObservationReference> getObservationReferences(
            CurriculumType curriculumType,
            List<SectionType> sectionTypes
    );
}
