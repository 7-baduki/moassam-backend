package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.SectionRegenerateInput;

import java.util.List;

public record SectionRegenerateRequest(
        String memo,
        List<KeywordRequest> keywords
) {

    public SectionRegenerateInput toInput() {
        return new SectionRegenerateInput(
                memo,
                keywords == null
                        ? List.of()
                        : keywords.stream()
                                .map(KeywordRequest::toInput)
                                .toList()
        );
    }
}
