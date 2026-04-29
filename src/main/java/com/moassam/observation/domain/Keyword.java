package com.moassam.observation.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

    private KeywordType type;
    private String value;

    public static Keyword create(
            KeywordType type,
            String value
    ) {
        Keyword keyword = new Keyword();

        keyword.type = type;
        keyword.value = value;

        return keyword;
    }
}
