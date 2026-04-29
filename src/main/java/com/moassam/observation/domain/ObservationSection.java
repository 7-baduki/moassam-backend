package com.moassam.observation.domain;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ObservationSection extends BaseEntity {

    private Long id;
    private Observation observation;
    private SectionType type;
    private String content;
    private boolean edited;

    public static ObservationSection create(
            SectionType type,
            String content
    ) {
        ObservationSection section = new ObservationSection();

        section.type = type;
        section.content = content;
        section.edited = false;

        return section;
    }

    void assignObservation(Observation observation) {
        this.observation = observation;
    }

    public void edit(String content) {
        this.content = content;
        this.edited = true;
    }

    public void replaceByRegeneratedContent(String content) {
        this.content = content;
        this.edited = false;
    }

    public boolean isSameId(Long sectionId) {
        return this.id != null && this.id.equals(sectionId);
    }

    public boolean isSameType(SectionType type) {
        return this.type == type;
    }
}
