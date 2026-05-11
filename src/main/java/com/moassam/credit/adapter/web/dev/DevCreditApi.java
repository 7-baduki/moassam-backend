package com.moassam.credit.adapter.web.dev;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.credit.adapter.web.dto.DevCreditChargeResponse;
import com.moassam.credit.application.provided.DevCreditCharger;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/dev/credit")
@RestController
public class DevCreditApi {

    private final DevCreditCharger devCreditCharger;

    @RequireAuth
    @PostMapping("/charge")
    public SuccessResponse<DevCreditChargeResponse> chargeDevCredit(
            @CurrentUserId Long userId
    ) {
        int balance = devCreditCharger.chargeDevCredit(userId);

        return SuccessResponse.of(DevCreditChargeResponse.from(balance));
    }
}
