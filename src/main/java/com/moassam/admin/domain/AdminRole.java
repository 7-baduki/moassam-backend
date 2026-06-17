package com.moassam.admin.domain;

public enum AdminRole {
    SUPER_ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }
}
