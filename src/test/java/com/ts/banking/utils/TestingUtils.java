package com.ts.banking.utils;

import com.ts.banking.enums.MovementType;
import com.ts.banking.enums.TransactionType;
import com.ts.banking.persistence.entities.Account;
import com.ts.banking.persistence.entities.AccountHistory;
import com.ts.banking.persistence.entities.Person;
import com.ts.banking.persistence.entities.Transaction;

import java.math.BigDecimal;

public class TestingUtils {

    public static Account createPersonAndAccount(Long id, String email, Long accountNo, BigDecimal balance) {
        Person person = Person.builder()
                .id(id)
                .firstName("Kobe")
                .lastName("Bryant")
                .email(email)
                .build();

        Account account = Account.builder()
                .id(id)
                .accountNo(accountNo)
                .enabled(true)
                .build();

        person.addAccount(account);

        Transaction transaction = createTransaction(account);

        account.addAccountHistory(AccountHistory.builder()
                .amount(BigDecimal.TEN)
                .balance(balance)
                .type(MovementType.COMING_IN)
                .transaction(transaction)
                .build());

        return account;
    }

    private static Transaction createTransaction(Account account) {
        return Transaction.builder().amount(BigDecimal.TEN)
                .transactionType(TransactionType.DEPOSIT)
                .destination(account).build();
    }

    public static Person createPersonAndAccount(String email, Long accountNo) {
        Person person = Person.builder()
                .firstName("Kobe")
                .lastName("Bryant")
                .email(email)
                .build();

        Account account = Account.builder()
                .accountNo(accountNo)
                .enabled(true)
                .build();

        person.addAccount(account);
        return person;
    }
}
