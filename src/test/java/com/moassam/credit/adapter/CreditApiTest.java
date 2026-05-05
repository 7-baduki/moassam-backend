package com.moassam.credit.adapter;

import com.moassam.credit.adapter.web.CreditApi;
import com.moassam.credit.application.provided.CreditFinder;
import com.moassam.credit.domain.CreditWallet;
import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreditApiTest extends RestDocsSupport {

    private final CreditFinder creditFinder = mock(CreditFinder.class);

    @Override
    protected Object initController() {
        return new CreditApi(creditFinder);
    }

    @Test
    void getCreditWallet() throws Exception {
        given(creditFinder.getWallet(any()))
                .willReturn(CreditWallet.create(1L, LocalDate.of(2026, 5, 6)));

        mockMvc.perform(get("/api/v1/credits"))
                .andExpect(status().isOk())
                .andDo(document("credit/get-credit-wallet",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                        fieldWithPath("data.balance").type(JsonFieldType.NUMBER).description("현재 남은 횟수"),
                                        fieldWithPath("data.dailyBonusChargedAmount").type(JsonFieldType.NUMBER).description("오늘 보상으로 충전된 횟수"),
                                        fieldWithPath("data.lastResetDate").type(JsonFieldType.STRING).description("마지막 일일 리셋 날짜")
                                )
                        )
                ));
    }
}
