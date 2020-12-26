package com.ts.banking.persistence;

import com.ts.banking.enums.MovementType;
import com.ts.banking.enums.TransactionType;
import com.ts.banking.persistence.entities.Account;
import com.ts.banking.persistence.entities.AccountHistory;
import com.ts.banking.persistence.entities.Person;
import com.ts.banking.persistence.entities.Transaction;
import com.ts.banking.persistence.repositories.AccountHistoryRepository;
import com.ts.banking.persistence.repositories.AccountRepository;
import com.ts.banking.persistence.repositories.PersonRepository;
import com.ts.banking.persistence.repositories.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountHistoryRepositoryTest {

    @Autowired
    private AccountHistoryRepository accountHistoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PersonRepository personRepository;

    @DisplayName("GIVEN account id with no account history WHEN findFirstByAccountIdOrderByIdDesc() getBalance() SHOULD return optional empty")
    @Test
    public void whenFindByAccountIdWithNoHistory_thenReturnOptionalEmpty() {
        Account account = createPersonAndAccount();
        personRepository.save(account.getPerson());

        assertThat(accountHistoryRepository.findFirstByAccountIdOrderByIdDesc(account.getId())).isEqualTo(Optional.empty());
    }

    @DisplayName("GIVEN account id with single history WHEN findFirstByAccountIdOrderByIdDesc() getBalance() SHOULD return account history balance")
    @Test
    public void whenFindByAccountIdWithHistory_thenReturnHistoryBalance() {

        Account account = createPersonAndAccount();
        personRepository.save(account.getPerson());

        Transaction transaction = createTransaction(account);
        transactionRepository.save(transaction);

        account.addAccountHistory(AccountHistory.builder()
                .amount(BigDecimal.TEN)
                .balance(BigDecimal.TEN)
                .type(MovementType.COMING_IN)
                .transaction(transaction)
                .build());

        accountRepository.save(account);

        assertThat(accountHistoryRepository.findFirstByAccountIdOrderByIdDesc(account.getId()).get().getAmount()).isEqualTo(BigDecimal.TEN);
    }

    @DisplayName("GIVEN account id with multiple history WHEN findFirstByAccountIdOrderByIdDesc() getBalance() SHOULD return latest account history balance")
    @Test
    public void whenFindByAccountIdWithMultipleHistory_thenReturnLatestHistoryBalance() {

        Account account = createPersonAndAccount();
        personRepository.save(account.getPerson());

        Transaction transaction = createTransaction(account);
        transactionRepository.save(transaction);

        account.addAccountHistory(AccountHistory.builder()
                .amount(BigDecimal.TEN)
                .balance(BigDecimal.TEN)
                .type(MovementType.COMING_IN)
                .transaction(transaction)
                .build());

        Transaction transaction2 = createTransaction(account);
        transactionRepository.save(transaction2);


        account.addAccountHistory(AccountHistory.builder()
                .amount(BigDecimal.TEN)
                .balance(BigDecimal.valueOf(20))
                .type(MovementType.COMING_IN)
                .transaction(transaction2)
                .build());

        accountRepository.save(account);

        assertThat(accountHistoryRepository.findFirstByAccountIdOrderByIdDesc(account.getId()).get().getBalance()).isEqualTo(BigDecimal.valueOf(20L));
    }

    private static Account createPersonAndAccount() {
        Person person = Person.builder()
                .firstName("Kobe")
                .lastName("Bryant")
                .email("kb24@gmail.com")
                .build();

        Account account = Account.builder()
                .accountNo(1875136135L)
                .enabled(true)
                .build();

        person.addAccount(account);
        return account;
    }

    private static Transaction createTransaction(Account account) {
        return Transaction.builder().amount(BigDecimal.TEN)
                .transactionType(TransactionType.DEPOSIT)
                .destination(account).build();
    }
}
