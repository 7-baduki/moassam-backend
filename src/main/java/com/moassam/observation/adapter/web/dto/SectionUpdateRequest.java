package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.application.command.SectionUpdateCommand;

public record SectionUpdateRequest(
        String content
) {

    public SectionUpdateCommand toCommand() {
        return new SectionUpdateCommand(content);
    }
}
