package com.moassam.admin.application;

import com.moassam.admin.application.dto.AdminDashboardSnapshot;
import com.moassam.admin.application.required.AdminDashboardQueryRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AdminDashboardServiceTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-10-25T05:30:00Z"),
            SEOUL
    );

    private final AdminDashboardQueryRepository queryRepository = mock(AdminDashboardQueryRepository.class);
    private final AdminDashboardService service = new AdminDashboardService(queryRepository, CLOCK);

    @Test
    void getDashboard_usesOneAsOfAndCompleteThirtyDayWindows() {
        var dashboard = service.getDashboard(30);

        assertThat(dashboard.asOf()).isEqualTo(LocalDateTime.of(2026, 10, 25, 14, 30));
        assertThat(dashboard.todayStart()).isEqualTo(LocalDateTime.of(2026, 10, 25, 0, 0));
        assertThat(dashboard.currentPeriod().startInclusive())
                .isEqualTo(LocalDateTime.of(2026, 9, 25, 0, 0));
        assertThat(dashboard.currentPeriod().endExclusive())
                .isEqualTo(LocalDateTime.of(2026, 10, 25, 0, 0));
        assertThat(dashboard.comparisonPeriod().startInclusive())
                .isEqualTo(LocalDateTime.of(2026, 8, 26, 0, 0));
        assertThat(dashboard.comparisonPeriod().endExclusive())
                .isEqualTo(LocalDateTime.of(2026, 9, 25, 0, 0));
    }

    @Test
    void getDashboard_acceptsOnlySupportedPeriods() {
        assertThat(service.getDashboard(7).periodDays()).isEqualTo(7);
        assertThat(service.getDashboard(90).periodDays()).isEqualTo(90);

        assertThatThrownBy(() -> service.getDashboard(14))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("periodDays must be one of 7, 30, 90");
    }

    @Test
    void getDashboard_calculatesMetricsAndKeepsPreCollectionDaysUnavailable() {
        LocalDateTime todayStart = LocalDateTime.of(2026, 10, 25, 0, 0);
        LocalDateTime currentStart = LocalDateTime.of(2026, 9, 25, 0, 0);
        LocalDateTime comparisonStart = LocalDateTime.of(2026, 8, 26, 0, 0);
        given(queryRepository.findCollectionState("USER_ACTIVITY"))
                .willReturn(new AdminDashboardQueryRepository.CollectionState(
                        LocalDate.of(2026, 10, 20), false
                ));
        given(queryRepository.findCollectionState("USER_LIFECYCLE"))
                .willReturn(new AdminDashboardQueryRepository.CollectionState(
                        LocalDate.of(2026, 9, 1), false
                ));
        given(queryRepository.countLifecycleEvents(
                "SIGNUP", todayStart, LocalDateTime.of(2026, 10, 25, 14, 30)
        )).willReturn(3L);
        given(queryRepository.countLifecycleEvents("SIGNUP", currentStart, todayStart)).willReturn(99L);
        given(queryRepository.countLifecycleEvents("SIGNUP", comparisonStart, currentStart)).willReturn(85L);
        given(queryRepository.countLifecycleEvents("WITHDRAWAL", currentStart, todayStart)).willReturn(16L);
        given(queryRepository.countUnusedWithdrawals(currentStart, todayStart)).willReturn(6L);
        given(queryRepository.countSignupUsersWithObservation(currentStart, todayStart)).willReturn(62L);
        given(queryRepository.countActiveUsers(LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26)))
                .willReturn(41L);
        given(queryRepository.countObservationCreated(
                LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26)
        )).willReturn(23L);
        given(queryRepository.countActiveUsers(
                LocalDate.of(2026, 9, 25), LocalDate.of(2026, 10, 25)
        )).willReturn(236L);
        given(queryRepository.countActiveUsers(
                LocalDate.of(2026, 10, 11), LocalDate.of(2026, 10, 18)
        )).willReturn(100L);
        given(queryRepository.countReturningUsers(
                LocalDate.of(2026, 10, 11), LocalDate.of(2026, 10, 18),
                LocalDate.of(2026, 10, 18), LocalDate.of(2026, 10, 25)
        )).willReturn(64L);
        given(queryRepository.findDailyActiveUsers(
                LocalDate.of(2026, 9, 25), LocalDate.of(2026, 10, 25)
        )).willReturn(List.of(new AdminDashboardQueryRepository.DailyCount(
                LocalDate.of(2026, 10, 20), 2
        )));
        given(queryRepository.getFeatureUsage(
                LocalDate.of(2026, 9, 25), LocalDate.of(2026, 10, 25)
        )).willReturn(new AdminDashboardQueryRepository.FeatureUsageCounts(56, 5, 4, 36));
        given(queryRepository.getGenerationCounts(
                LocalDate.of(2026, 9, 25), LocalDate.of(2026, 10, 25)
        )).willReturn(new AdminDashboardQueryRepository.GenerationCounts(1088, 297));
        given(queryRepository.getGenerationCounts(
                LocalDate.of(2026, 10, 24), LocalDate.of(2026, 10, 25)
        )).willReturn(new AdminDashboardQueryRepository.GenerationCounts(69, 31));
        given(queryRepository.getGenerationCounts(
                LocalDate.of(2026, 10, 17), LocalDate.of(2026, 10, 24)
        )).willReturn(new AdminDashboardQueryRepository.GenerationCounts(790, 210));

        var dashboard = service.getDashboard(30);

        assertThat(dashboard.today().activeUsers()).isEqualTo(41);
        assertThat(dashboard.cards().signupChangePercent()).isCloseTo(16.47, within(0.01));
        assertThat(dashboard.cards().observationWritingRatePercent()).isCloseTo(62.63, within(0.01));
        assertThat(dashboard.dau().sevenDayRevisitRatePercent()).isNull();
        assertThat(dashboard.dau().current().get(0).count()).isNull();
        assertThat(dashboard.dau().current().stream()
                .filter(point -> point.date().equals(LocalDate.of(2026, 10, 20)))
                .findFirst().orElseThrow().count()).isEqualTo(2L);
        assertThat(dashboard.dau().availability().status())
                .isEqualTo(AdminDashboardSnapshot.AvailabilityStatus.PARTIAL);
        assertThat(dashboard.regeneration().warning().yesterdayRatePercent()).isNull();
        assertThat(dashboard.regeneration().warning().previousSevenDayRatePercent()).isNull();
        assertThat(dashboard.regeneration().warning().warning()).isFalse();
    }

    @Test
    void getDashboard_marksSevenDayRegenerationPartialWhenWarningBaselineIsMissing() {
        given(queryRepository.findCollectionState("USER_ACTIVITY"))
                .willReturn(new AdminDashboardQueryRepository.CollectionState(
                        LocalDate.of(2026, 10, 18), false
                ));

        var dashboard = service.getDashboard(7);

        assertThat(dashboard.regeneration().availability().status())
                .isEqualTo(AdminDashboardSnapshot.AvailabilityStatus.PARTIAL);
        assertThat(dashboard.regeneration().availability().coveredFrom())
                .isEqualTo(LocalDate.of(2026, 10, 18));
        assertThat(dashboard.regeneration().warning().warning()).isFalse();
        assertThat(dashboard.regeneration().warning().yesterdayRatePercent()).isNull();
        assertThat(dashboard.regeneration().warning().previousSevenDayRatePercent()).isNull();
    }

    @Test
    void getDashboardEmitsRegenerationWarningWhenTheWholeBaselineIsCovered() {
        given(queryRepository.findCollectionState("USER_ACTIVITY"))
                .willReturn(new AdminDashboardQueryRepository.CollectionState(
                        LocalDate.of(2026, 10, 17), false
                ));
        given(queryRepository.getGenerationCounts(
                LocalDate.of(2026, 10, 24), LocalDate.of(2026, 10, 25)
        )).willReturn(new AdminDashboardQueryRepository.GenerationCounts(69, 31));
        given(queryRepository.getGenerationCounts(
                LocalDate.of(2026, 10, 17), LocalDate.of(2026, 10, 24)
        )).willReturn(new AdminDashboardQueryRepository.GenerationCounts(790, 210));

        var dashboard = service.getDashboard(7);

        assertThat(dashboard.regeneration().availability().status())
                .isEqualTo(AdminDashboardSnapshot.AvailabilityStatus.AVAILABLE);
        assertThat(dashboard.regeneration().warning().yesterdayRatePercent()).isEqualTo(31.0);
        assertThat(dashboard.regeneration().warning().previousSevenDayRatePercent()).isEqualTo(21.0);
        assertThat(dashboard.regeneration().warning().warning()).isTrue();
    }
}
