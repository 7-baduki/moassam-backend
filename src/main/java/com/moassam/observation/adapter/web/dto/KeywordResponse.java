package com.moassam.observation.adapter.web.dto;

import com.moassam.observation.application.result.KeywordResult;
import com.moassam.observation.domain.KeywordType;

public record KeywordResponse(
        KeywordType type,
        String value
) {

    public static KeywordResponse from(KeywordResult result) {
        return new KeywordResponse(
                result.type(),
                result.value()
        );
    }
}
