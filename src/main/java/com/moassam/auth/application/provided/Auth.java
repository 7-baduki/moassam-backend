package com.moassam.auth.application.provided;

public interface Auth {

    void logout(Long userId);

    String refresh(String refreshToken);

    void withdraw(Long userId);
}