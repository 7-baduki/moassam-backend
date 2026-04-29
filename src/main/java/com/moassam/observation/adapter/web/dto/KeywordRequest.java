package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.application.command.KeywordCommand;
import com.moassam.observation.domain.KeywordType;

public record KeywordRequest(
        KeywordType type,
        String value
) {

    public KeywordCommand toCommand() {
        return new KeywordCommand(type, value);
    }
}
