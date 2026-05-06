package com.moassam.credit.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.credit.adapter.web.dto.CreditWalletResponse;
import com.moassam.credit.application.provided.CreditFinder;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/credits")
@RestController
public class CreditApi {

    private final CreditFinder creditFinder;

    @RequireAuth
    @GetMapping
    public SuccessResponse<CreditWalletResponse> getCreditWallet(
            @CurrentUserId Long userId
    ) {
        return SuccessResponse.of(
                CreditWalletResponse.from(creditFinder.getWallet(userId))
        );
    }
}
