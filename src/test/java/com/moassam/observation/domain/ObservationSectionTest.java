package com.moassam.observation.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservationSectionTest {

    @Test
    void createObservationSection() {
        ObservationSection section = ObservationSection.create(
                1L,
                SectionType.COMMUNICATION,
                "의사소통 관찰 내용"
        );

        assertThat(section.getObservationId()).isEqualTo(1L);
        assertThat(section.getSectionType()).isEqualTo(SectionType.COMMUNICATION);
        assertThat(section.getContent()).isEqualTo("의사소통 관찰 내용");
        assertThat(section.getDisplayOrder()).isEqualTo(SectionType.COMMUNICATION.getDisplayOrder());
    }
}
