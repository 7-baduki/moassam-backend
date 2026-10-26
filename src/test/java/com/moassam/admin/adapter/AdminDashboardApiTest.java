package com.moassam.admin.adapter;

import com.moassam.admin.application.AdminDashboardService;
import com.moassam.admin.application.dto.AdminDashboardSnapshot;
import com.moassam.admin.application.dto.DashboardPeriod;
import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminDashboardApiTest extends RestDocsSupport {

    private final AdminDashboardService service = mock(AdminDashboardService.class);

    @Override
    protected Object initController() {
        return new AdminDashboardApi(service);
    }

    @Test
    void getDashboard() throws Exception {
        LocalDateTime todayStart = LocalDateTime.of(2026, 10, 25, 0, 0);
        var availability = new AdminDashboardSnapshot.Availability(
                AdminDashboardSnapshot.AvailabilityStatus.AVAILABLE,
                LocalDate.of(2026, 9, 25),
                LocalDate.of(2026, 10, 24),
                false
        );
        var snapshot = new AdminDashboardSnapshot(
                LocalDateTime.of(2026, 10, 25, 14, 30),
                todayStart,
                30,
                new DashboardPeriod(todayStart.minusDays(30), todayStart),
                new DashboardPeriod(todayStart.minusDays(60), todayStart.minusDays(30)),
                new AdminDashboardSnapshot.Today(3, 41, 23),
                new AdminDashboardSnapshot.Cards(99, 16.0, 63.0, 16, 6, availability),
                new AdminDashboardSnapshot.Dau(
                        List.of(new AdminDashboardSnapshot.DailyPoint(LocalDate.of(2026, 10, 24), 61L)),
                        List.of(new AdminDashboardSnapshot.DailyPoint(LocalDate.of(2026, 9, 24), 58L)),
                        236, 64.0, availability
                ),
                new AdminDashboardSnapshot.FeatureUsage(
                        new AdminDashboardSnapshot.UsageSegment(56, 56.0),
                        new AdminDashboardSnapshot.UsageSegment(5, 5.0),
                        new AdminDashboardSnapshot.UsageSegment(4, 4.0),
                        new AdminDashboardSnapshot.UsageSegment(36, 36.0),
                        availability
                ),
                new AdminDashboardSnapshot.Regeneration(
                        1088, 297, 21.4,
                        new AdminDashboardSnapshot.RegenerationWarning(31.0, 21.0, true),
                        availability
                ),
                new AdminDashboardSnapshot.AiUsage(1385, 1300, 85, 100, 200, 300, 1200.0, availability),
                List.of(new AdminDashboardSnapshot.RecentPost(
                        5, "FREE", "수행평가 루브릭 공유합니다", 4,
                        LocalDateTime.of(2026, 10, 23, 16, 10)
                )),
                new AdminDashboardSnapshot.DataQuality(false)
        );
        given(service.getDashboard(30)).willReturn(snapshot);

        mockMvc.perform(get("/api/v1/admin/dashboard").param("periodDays", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.periodDays").value(30))
                .andExpect(jsonPath("$.data.today.activeUsers").value(41))
                .andExpect(jsonPath("$.data.cards.observationWritingRatePercent").value(63.0))
                .andExpect(jsonPath("$.data.dau.current[0].count").value(61))
                .andExpect(jsonPath("$.data.regeneration.warning.warning").value(true))
                .andExpect(jsonPath("$.data.aiUsage.totalTokens").value(300))
                .andExpect(jsonPath("$.data.recentPosts[0].postId").value(5))
                .andDo(document("admin/dashboard/get-dashboard",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        queryParameters(
                                parameterWithName("periodDays")
                                        .description("완결 분석 기간: 7, 30, 90일. 기본값 30일")
                                        .optional()
                        ),
                        responseFields(CommonDocumentation.successResponseFields(
                                fieldWithPath("data.asOf").type(JsonFieldType.STRING)
                                        .description("한 요청에서 공통으로 사용하는 Asia/Seoul 기준 시각"),
                                fieldWithPath("data.todayStart").type(JsonFieldType.STRING)
                                        .description("오늘 실시간 집계의 시작 시각(Asia/Seoul 00:00)"),
                                fieldWithPath("data.periodDays").type(JsonFieldType.NUMBER)
                                        .description("선택한 완결 분석 기간"),
                                fieldWithPath("data.currentPeriod.startInclusive").type(JsonFieldType.STRING)
                                        .description("현재 분석 기간 시작 시각, 포함"),
                                fieldWithPath("data.currentPeriod.endExclusive").type(JsonFieldType.STRING)
                                        .description("현재 분석 기간 종료 시각, 미포함(오늘 00:00)"),
                                fieldWithPath("data.comparisonPeriod.startInclusive").type(JsonFieldType.STRING)
                                        .description("직전 동일 길이 비교 기간 시작 시각, 포함"),
                                fieldWithPath("data.comparisonPeriod.endExclusive").type(JsonFieldType.STRING)
                                        .description("직전 동일 길이 비교 기간 종료 시각, 미포함"),
                                fieldWithPath("data.today.signups").type(JsonFieldType.NUMBER)
                                        .description("오늘 신규 가입자 수"),
                                fieldWithPath("data.today.activeUsers").type(JsonFieldType.NUMBER)
                                        .description("오늘 고유 활성 사용자 수"),
                                fieldWithPath("data.today.observationsCreated").type(JsonFieldType.NUMBER)
                                        .description("오늘 성공적으로 저장된 관찰일지 생성 수"),
                                fieldWithPath("data.cards.signups").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 신규 가입자 수"),
                                fieldWithPath("data.cards.signupChangePercent").type(JsonFieldType.NUMBER)
                                        .description("직전 기간 대비 가입자 증감률(%). 직전 값이 0이면 null"),
                                fieldWithPath("data.cards.observationWritingRatePercent").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 신규 가입자의 같은 기간 관찰일지 작성률(%). 가입자가 0명이면 null"),
                                fieldWithPath("data.cards.withdrawals").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 탈퇴자 수"),
                                fieldWithPath("data.cards.unusedWithdrawals").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 의미 있는 이용 이력이 없는 탈퇴자 수"),
                                fieldWithPath("data.cards.lifecycleAvailability.status").type(JsonFieldType.STRING)
                                        .description("생애주기 지표 가용성: AVAILABLE, PARTIAL, COLLECTING"),
                                fieldWithPath("data.cards.lifecycleAvailability.coveredFrom").type(JsonFieldType.STRING)
                                        .description("실제로 수집된 시작일. COLLECTING이면 null"),
                                fieldWithPath("data.cards.lifecycleAvailability.coveredTo").type(JsonFieldType.STRING)
                                        .description("실제로 수집된 종료일. COLLECTING이면 null"),
                                fieldWithPath("data.cards.lifecycleAvailability.degraded").type(JsonFieldType.BOOLEAN)
                                        .description("수집 누락이 확인된 생애주기 지표 여부"),
                                fieldWithPath("data.dau.current").type(JsonFieldType.ARRAY)
                                        .description("현재 기간 일별 활성 사용자 시계열"),
                                fieldWithPath("data.dau.current[].date").type(JsonFieldType.STRING)
                                        .description("현재 기간 날짜"),
                                fieldWithPath("data.dau.current[].count").type(JsonFieldType.NUMBER)
                                        .description("해당 날짜 고유 활성 사용자 수. 수집 전 날짜는 null"),
                                fieldWithPath("data.dau.comparison").type(JsonFieldType.ARRAY)
                                        .description("직전 동일 기간 일별 활성 사용자 시계열"),
                                fieldWithPath("data.dau.comparison[].date").type(JsonFieldType.STRING)
                                        .description("비교 기간 날짜"),
                                fieldWithPath("data.dau.comparison[].count").type(JsonFieldType.NUMBER)
                                        .description("해당 날짜 고유 활성 사용자 수. 수집 전 날짜는 null"),
                                fieldWithPath("data.dau.uniqueActiveUsers").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 고유 활성 사용자 수"),
                                fieldWithPath("data.dau.sevenDayRevisitRatePercent").type(JsonFieldType.NUMBER)
                                        .description("최근 완결 7일 재방문율(%). 비교할 수 없거나 분모가 0이면 null"),
                                fieldWithPath("data.dau.availability.status").type(JsonFieldType.STRING)
                                        .description("DAU 지표 가용성: AVAILABLE, PARTIAL, COLLECTING"),
                                fieldWithPath("data.dau.availability.coveredFrom").type(JsonFieldType.STRING)
                                        .description("DAU 실제 수집 시작일. COLLECTING이면 null"),
                                fieldWithPath("data.dau.availability.coveredTo").type(JsonFieldType.STRING)
                                        .description("DAU 실제 수집 종료일. COLLECTING이면 null"),
                                fieldWithPath("data.dau.availability.degraded").type(JsonFieldType.BOOLEAN)
                                        .description("수집 누락이 확인된 DAU 지표 여부"),
                                fieldWithPath("data.featureUsage.observationOnly.count").type(JsonFieldType.NUMBER)
                                        .description("관찰일지만 이용한 사용자-일 수"),
                                fieldWithPath("data.featureUsage.observationOnly.percent").type(JsonFieldType.NUMBER)
                                        .description("관찰일지만 이용한 비율(%). 전체가 0이면 null"),
                                fieldWithPath("data.featureUsage.observationAndCommunity.count").type(JsonFieldType.NUMBER)
                                        .description("관찰일지와 커뮤니티를 모두 이용한 사용자-일 수"),
                                fieldWithPath("data.featureUsage.observationAndCommunity.percent").type(JsonFieldType.NUMBER)
                                        .description("관찰일지와 커뮤니티를 모두 이용한 비율(%). 전체가 0이면 null"),
                                fieldWithPath("data.featureUsage.communityOnly.count").type(JsonFieldType.NUMBER)
                                        .description("커뮤니티만 이용한 사용자-일 수"),
                                fieldWithPath("data.featureUsage.communityOnly.percent").type(JsonFieldType.NUMBER)
                                        .description("커뮤니티만 이용한 비율(%). 전체가 0이면 null"),
                                fieldWithPath("data.featureUsage.browseOnly.count").type(JsonFieldType.NUMBER)
                                        .description("기능 사용 없이 둘러보기만 한 사용자-일 수"),
                                fieldWithPath("data.featureUsage.browseOnly.percent").type(JsonFieldType.NUMBER)
                                        .description("둘러보기만 한 비율(%). 전체가 0이면 null"),
                                fieldWithPath("data.featureUsage.availability.status").type(JsonFieldType.STRING)
                                        .description("기능 이용 지표 가용성: AVAILABLE, PARTIAL, COLLECTING"),
                                fieldWithPath("data.featureUsage.availability.coveredFrom").type(JsonFieldType.STRING)
                                        .description("기능 이용 지표 실제 수집 시작일. COLLECTING이면 null"),
                                fieldWithPath("data.featureUsage.availability.coveredTo").type(JsonFieldType.STRING)
                                        .description("기능 이용 지표 실제 수집 종료일. COLLECTING이면 null"),
                                fieldWithPath("data.featureUsage.availability.degraded").type(JsonFieldType.BOOLEAN)
                                        .description("수집 누락이 확인된 기능 이용 지표 여부"),
                                fieldWithPath("data.regeneration.created").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 성공적으로 저장된 최초 생성 수"),
                                fieldWithPath("data.regeneration.regenerated").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 성공적으로 저장된 재생성 수"),
                                fieldWithPath("data.regeneration.ratePercent").type(JsonFieldType.NUMBER)
                                        .description("전체 생성·재생성 중 재생성 비율(%). 전체가 0이면 null"),
                                fieldWithPath("data.regeneration.warning.yesterdayRatePercent").type(JsonFieldType.NUMBER)
                                        .description("어제 재생성률(%). 전체가 0이면 null"),
                                fieldWithPath("data.regeneration.warning.previousSevenDayRatePercent").type(JsonFieldType.NUMBER)
                                        .description("어제 이전 7개 완결일의 가중 재생성률(%). 전체가 0이면 null"),
                                fieldWithPath("data.regeneration.warning.warning").type(JsonFieldType.BOOLEAN)
                                        .description("어제 호출 10건 이상이고 7일 기준보다 10%p 이상 높은지 여부"),
                                fieldWithPath("data.regeneration.availability.status").type(JsonFieldType.STRING)
                                        .description("재생성 지표 가용성: AVAILABLE, PARTIAL, COLLECTING"),
                                fieldWithPath("data.regeneration.availability.coveredFrom").type(JsonFieldType.STRING)
                                        .description("재생성 지표 실제 수집 시작일. COLLECTING이면 null"),
                                fieldWithPath("data.regeneration.availability.coveredTo").type(JsonFieldType.STRING)
                                        .description("재생성 지표 실제 수집 종료일. COLLECTING이면 null"),
                                fieldWithPath("data.regeneration.availability.degraded").type(JsonFieldType.BOOLEAN)
                                        .description("수집 누락이 확인된 재생성 지표 여부"),
                                fieldWithPath("data.aiUsage.calls").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 AI 호출 수"),
                                fieldWithPath("data.aiUsage.successes").type(JsonFieldType.NUMBER)
                                        .description("관찰일지 저장까지 성공한 AI 호출 수"),
                                fieldWithPath("data.aiUsage.failures").type(JsonFieldType.NUMBER)
                                        .description("저장 완료되지 않은 AI 호출 수"),
                                fieldWithPath("data.aiUsage.inputTokens").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 입력 토큰 합계"),
                                fieldWithPath("data.aiUsage.outputTokens").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 출력 토큰 합계"),
                                fieldWithPath("data.aiUsage.totalTokens").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 전체 토큰 합계"),
                                fieldWithPath("data.aiUsage.averageDurationMs").type(JsonFieldType.NUMBER)
                                        .description("현재 기간 AI 호출 평균 처리시간(ms). 호출이 없으면 null"),
                                fieldWithPath("data.aiUsage.availability.status").type(JsonFieldType.STRING)
                                        .description("AI 사용량 지표 가용성: AVAILABLE, PARTIAL, COLLECTING"),
                                fieldWithPath("data.aiUsage.availability.coveredFrom").type(JsonFieldType.STRING)
                                        .description("AI 사용량 실제 수집 시작일. COLLECTING이면 null"),
                                fieldWithPath("data.aiUsage.availability.coveredTo").type(JsonFieldType.STRING)
                                        .description("AI 사용량 실제 수집 종료일. COLLECTING이면 null"),
                                fieldWithPath("data.aiUsage.availability.degraded").type(JsonFieldType.BOOLEAN)
                                        .description("수집 누락이 확인된 AI 사용량 지표 여부"),
                                fieldWithPath("data.recentPosts").type(JsonFieldType.ARRAY)
                                        .description("기간과 무관한 최신 게시글 5개"),
                                fieldWithPath("data.recentPosts[].postId").type(JsonFieldType.NUMBER)
                                        .description("게시글 ID"),
                                fieldWithPath("data.recentPosts[].category").type(JsonFieldType.STRING)
                                        .description("게시글 카테고리"),
                                fieldWithPath("data.recentPosts[].title").type(JsonFieldType.STRING)
                                        .description("게시글 제목"),
                                fieldWithPath("data.recentPosts[].commentCount").type(JsonFieldType.NUMBER)
                                        .description("댓글 수"),
                                fieldWithPath("data.recentPosts[].createdAt").type(JsonFieldType.STRING)
                                        .description("게시글 생성 시각"),
                                fieldWithPath("data.dataQuality.degraded").type(JsonFieldType.BOOLEAN)
                                        .description("수집 누락이 확인된 지표 존재 여부")
                        ))
                ));
    }

    @Test
    void getDashboard_rejectsUnsupportedPeriod() throws Exception {
        given(service.getDashboard(14))
                .willThrow(new IllegalArgumentException("periodDays must be one of 7, 30, 90"));

        mockMvc.perform(get("/api/v1/admin/dashboard").param("periodDays", "14"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.properties.code").value("INVALID_REQUEST"));
    }
}
