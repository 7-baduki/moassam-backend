package com.moassam.credit.adapter;

import com.moassam.credit.adapter.web.dev.DevCreditApi;
import com.moassam.credit.application.provided.DevCreditCharger;
import com.moassam.docs.ApiDocumentUtils;
import com.moassam.docs.CommonDocumentation;
import com.moassam.docs.RestDocsSupport;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DevCreditApiTest extends RestDocsSupport {

    private final DevCreditCharger devCreditCharger = mock(DevCreditCharger.class);

    @Override
    protected Object initController() {
        return new DevCreditApi(devCreditCharger);
    }

    @Test
    void chargeDevCredit() throws Exception {
        given(devCreditCharger.chargeDevCredit(any()))
                .willReturn(30);

        mockMvc.perform(post("/dev/credit/charge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(30))
                .andDo(document("credit/dev-charge-credit",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                CommonDocumentation.successResponseFields(
                                        fieldWithPath("data.balance").type(JsonFieldType.NUMBER).description("현재 횟수 잔량")
                                )
                        )
                ));

        then(devCreditCharger).should().chargeDevCredit(any());
    }
}
