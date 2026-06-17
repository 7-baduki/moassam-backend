package com.moassam.admin.application.provided;

import com.moassam.admin.application.dto.AdminLoginResult;

public interface AdminAuth {
    AdminLoginResult login(String username, String password);
    String refresh(String refreshToken);
    void logout(Long adminAccountId);
}
