package com.moassam.observation.domain;

import lombok.Getter;

@Getter
public enum SectionType {
    PHYSICAL_HEALTH(1),
    COMMUNICATION(2),
    SOCIAL_RELATIONSHIP(3),
    ART_EXPERIENCE(4),
    NATURE_EXPLORATION(5);

    private final int displayOrder;

    SectionType(int displayOrder) {
        this.displayOrder = displayOrder;
    }

}
