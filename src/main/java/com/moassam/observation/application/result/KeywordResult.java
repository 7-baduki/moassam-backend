package com.moassam.observation.application.result;

import com.moassam.observation.domain.Keyword;
import com.moassam.observation.domain.KeywordType;

public record KeywordResult(
        KeywordType type,
        String value
) {

    public static KeywordResult from(Keyword keyword) {
        return new KeywordResult(
                keyword.getType(),
                keyword.getValue()
        );
    }
}
