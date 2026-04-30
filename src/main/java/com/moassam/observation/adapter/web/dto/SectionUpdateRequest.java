package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.SectionUpdateInput;

public record SectionUpdateRequest(
        String content
) {

    public SectionUpdateInput toInput() {
        return new SectionUpdateInput(content);
    }
}
