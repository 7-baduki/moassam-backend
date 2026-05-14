package com.moassam.credit.application.required;

import com.moassam.credit.domain.CreditWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CreditWalletRepository extends Repository<CreditWallet, Long>{

    CreditWallet save(CreditWallet creditWallet);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from CreditWallet w where w.userId = :userId")
    Optional<CreditWallet> findByUserIdForUpdate(Long userId);

    void deleteByUserId(Long userId);
}
