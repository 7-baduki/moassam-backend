package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.domain.Keyword;
import com.moassam.observation.domain.KeywordType;

public record KeywordResponse(
        KeywordType type,
        String value
) {

    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(
                keyword.getType(),
                keyword.getValue()
        );
    }
}
