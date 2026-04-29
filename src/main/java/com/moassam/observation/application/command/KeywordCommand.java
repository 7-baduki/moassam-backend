package com.moassam.observation.application.command;

import com.moassam.observation.domain.KeywordType;

public record KeywordCommand(
        KeywordType type,
        String value
) {
}
