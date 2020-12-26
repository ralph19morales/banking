package com.ts.banking.services;

import com.ts.banking.persistence.entities.Account;
import com.ts.banking.persistence.repositories.AccountHistoryRepository;
import com.ts.banking.persistence.repositories.AccountRepository;
import com.ts.banking.utils.TestingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@DisplayName("Account Service Tests")
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountHistoryRepository accountHistoryRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        Account account = TestingUtils.createPersonAndAccount(1L, "source@gmail.com", 12345L, BigDecimal.TEN);

        lenient().when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        lenient().when(accountHistoryRepository.findFirstByAccountIdOrderByIdDesc(1L))
                .thenReturn(Optional.of(account.getHistory().get(0)));

    }

    @DisplayName("Given valid account id THEN return valid balance")
    @Test
    public void whenGivenValidAccountId_thenReturnBalance() {
        BigDecimal balance = accountService.getAccountBalance(1L);
        assertThat(balance).isEqualTo(BigDecimal.TEN);
    }

    @DisplayName("Given invalid account id THEN return zero")
    @Test
    public void whenGivenInvalidAccountId_thenReturnZero() {
        BigDecimal balance = accountService.getAccountBalance(2L);
        assertThat(balance).isEqualTo(BigDecimal.ZERO);
    }

    @DisplayName("Given valid account id THEN return account summary")
    @Test
    public void whenGivenValidAccountId_thenReturnAccountSummary() {
        AccountService.AccountSummary summary = accountService.getAccountSummary(1L);
        assertThat(summary.getBalance()).isEqualTo(BigDecimal.TEN);
        assertThat(summary.getEmail()).isEqualTo("source@gmail.com");
    }

    @DisplayName("Given invalid account id THEN throw error")
    @Test
    public void whenGivenInvalidAccountId_thenThrowError() {
        Assertions.assertThrows(RuntimeException.class, () -> accountService.getAccountSummary(2L));
    }
}
