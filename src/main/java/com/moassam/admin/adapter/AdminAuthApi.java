package com.moassam.admin.adapter;

import com.moassam.admin.adapter.dto.AdminLoginRequest;
import com.moassam.admin.application.dto.AdminLoginResult;
import com.moassam.admin.application.provided.AdminAuth;
import com.moassam.auth.adapter.security.cookie.HttpOnlyCookie;
import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.shared.web.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/admin/auth")
@RestController
public class AdminAuthApi {

    private final AdminAuth adminAuth;
    private final HttpOnlyCookie adminAccessTokenCookie;
    private final HttpOnlyCookie adminRefreshTokenCookie;

    public AdminAuthApi(
            AdminAuth adminAuth,
            @Qualifier("adminAccessTokenCookie") HttpOnlyCookie adminAccessTokenCookie,
            @Qualifier("adminRefreshTokenCookie") HttpOnlyCookie adminRefreshTokenCookie
    ) {
        this.adminAuth = adminAuth;
        this.adminAccessTokenCookie = adminAccessTokenCookie;
        this.adminRefreshTokenCookie = adminRefreshTokenCookie;
    }

    @PostMapping("/login")
    public SuccessResponse<Void> login(
            @RequestBody AdminLoginRequest request,
            HttpServletResponse response
    ) {
        AdminLoginResult result = adminAuth.login(request.username(), request.password());

        adminAccessTokenCookie.clearAll(response);
        adminRefreshTokenCookie.clearAll(response);

        adminAccessTokenCookie.add(response, result.accessToken());
        adminRefreshTokenCookie.add(response, result.refreshToken());

        return SuccessResponse.of(null);
    }

    @PostMapping("/refresh")
    public SuccessResponse<Void> refresh(
            @CookieValue(value = "adminRefreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        try {
            String accessToken = adminAuth.refresh(refreshToken);
            adminAccessTokenCookie.add(response, accessToken);

            return SuccessResponse.of(null);
        } catch (RuntimeException e) {
            adminAccessTokenCookie.clearAll(response);
            adminRefreshTokenCookie.clearAll(response);
            throw e;
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(
            @CurrentUserId Long adminAccountId,
            HttpServletResponse response
    ) {
        adminAuth.logout(adminAccountId);

        adminAccessTokenCookie.clearAll(response);
        adminRefreshTokenCookie.clearAll(response);
    }
}
