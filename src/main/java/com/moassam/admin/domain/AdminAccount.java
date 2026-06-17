package com.moassam.admin.domain;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminAccount extends BaseEntity {

    private Long id;
    private String username;
    private String passwordHash;
    private AdminRole role;
    private boolean enabled;
    private LocalDateTime lastLoginAt;

    public static AdminAccount createSuperAdmin(String username, String passwordHash) {
        AdminAccount admin = new AdminAccount();
        admin.username = username;
        admin.passwordHash = passwordHash;
        admin.role = AdminRole.SUPER_ADMIN;
        admin.enabled = true;
        return admin;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean isDisabled() {
        return !enabled;
    }
}
