package com.moassam.admin.application;

import com.moassam.admin.application.required.AdminAccountRepository;
import com.moassam.admin.domain.AdminAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class AdminBootstrap implements ApplicationRunner {
    private final AdminAccountRepository adminAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.initial.username:}")
    private String initialUsername;

    @Value("${admin.initial.password:}")
    private String initialPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(initialUsername) || !StringUtils.hasText(initialPassword)) {
            return;
        }

        if(adminAccountRepository.existsByUsername(initialUsername)) {
            return;
        }

        String passwordHash = passwordEncoder.encode(initialPassword);

        adminAccountRepository.save(AdminAccount.createSuperAdmin(initialUsername, passwordHash));
    }
}
