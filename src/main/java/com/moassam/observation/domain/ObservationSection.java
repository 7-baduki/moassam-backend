package com.moassam.observation.domain;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ObservationSection extends BaseEntity {

    private Long id;
    private Long observationId;
    private SectionType sectionType;
    private String content;
    private int displayOrder;

    public static ObservationSection create(
            Long observationId,
            SectionType sectionType,
            String content
    ) {
        ObservationSection section = new ObservationSection();

        section.observationId = observationId;
        section.sectionType = sectionType;
        section.content = content;
        section.displayOrder = sectionType.getDisplayOrder();

        return section;
    }
}
