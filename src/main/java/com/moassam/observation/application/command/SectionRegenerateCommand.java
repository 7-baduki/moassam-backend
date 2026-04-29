package com.moassam.observation.application.command;

import java.util.List;

public record SectionRegenerateCommand(
        String memo,
        List<KeywordCommand> keywords
) {
}
