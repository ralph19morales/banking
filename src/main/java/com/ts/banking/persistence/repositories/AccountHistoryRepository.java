package com.ts.banking.persistence.repositories;

import com.ts.banking.persistence.entities.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {

    Optional<AccountHistory> findFirstByAccountIdOrderByIdDesc(Long accountId);
}
