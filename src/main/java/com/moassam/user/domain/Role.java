package com.moassam.user.domain;

public enum Role {
    TEACHER;

    public String authority() {
        return "ROLE_" + name();
    }
}