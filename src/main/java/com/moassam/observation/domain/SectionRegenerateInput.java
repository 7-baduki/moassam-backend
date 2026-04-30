package com.moassam.observation.domain;

import java.util.List;

public record SectionRegenerateInput(
        String memo,
        List<KeywordInput> keywords
) {
}
