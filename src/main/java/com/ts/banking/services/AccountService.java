package com.ts.banking.services;

import com.ts.banking.persistence.entities.Account;
import com.ts.banking.persistence.entities.Person;
import com.ts.banking.persistence.repositories.AccountHistoryRepository;
import com.ts.banking.persistence.repositories.AccountRepository;
import com.ts.banking.persistence.repositories.PersonRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final PersonRepository personRepository;
    private final AccountRepository accountRepository;
    private final AccountHistoryRepository accountHistoryRepository;

    public BigDecimal getAccountBalance(Long accountId) {
        return accountHistoryRepository.findFirstByAccountIdOrderByIdDesc(accountId)
                .map(history -> history.getBalance())
                .orElse(BigDecimal.ZERO);
    }

    public AccountSummary getAccountSummary(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account doesnt exist"));
        return AccountSummary.builder()
                .id(accountId)
                .balance(getAccountBalance(accountId))
                .name(account.getPerson().getFullName())
                .email(account.getPerson().getEmail())
                .build();
    }

    public AccountSummary createAccount(CreateAccountReq req) {
        Person person = Person.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .build();

        Account account = Account.builder()
                .accountNo(Calendar.getInstance().getTimeInMillis())
                .enabled(true)
                .build();

        person.addAccount(account);
        personRepository.save(person);

        return AccountSummary.builder()
                .id(account.getId())
                .balance(BigDecimal.ZERO)
                .name(account.getPerson().getFullName())
                .email(account.getPerson().getEmail())
                .build();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountSummary {
        private Long id;
        private BigDecimal balance;
        private String name;
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateAccountReq {
        private String lastName;
        private String firstName;
        private String email;
    }
}
