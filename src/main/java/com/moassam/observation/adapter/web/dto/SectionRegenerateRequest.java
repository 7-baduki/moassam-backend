package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.application.command.SectionRegenerateCommand;

import java.util.List;

public record SectionRegenerateRequest(
        String memo,
        List<KeywordRequest> keywords
) {

    public SectionRegenerateCommand toCommand() {
        return new SectionRegenerateCommand(
                memo,
                keywords == null
                        ? List.of()
                        : keywords.stream()
                                .map(KeywordRequest::toCommand)
                                .toList()
        );
    }
}
