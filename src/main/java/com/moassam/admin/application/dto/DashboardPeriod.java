package com.moassam.admin.application.dto;

import java.time.LocalDateTime;

public record DashboardPeriod(
        LocalDateTime startInclusive,
        LocalDateTime endExclusive
) {
}
