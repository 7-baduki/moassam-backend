package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.KeywordInput;
import com.moassam.observation.domain.KeywordType;

public record KeywordRequest(
        KeywordType type,
        String value
) {

    public KeywordInput toInput() {
        return new KeywordInput(type, value);
    }
}
