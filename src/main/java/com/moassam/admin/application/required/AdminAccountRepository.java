package com.moassam.admin.application.required;

import com.moassam.admin.domain.AdminAccount;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AdminAccountRepository extends Repository<AdminAccount, Long> {

    AdminAccount save(AdminAccount adminAccount);

    Optional<AdminAccount> findById(Long id);

    Optional<AdminAccount> findByUsername(String username);

    boolean existsByUsername(String username);
}