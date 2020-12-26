package com.ts.banking.commands;

import com.ts.banking.commands.withdrawal.WithdrawalCommand;
import com.ts.banking.commands.withdrawal.WithdrawalReq;
import com.ts.banking.commands.withdrawal.WithdrawalResp;
import com.ts.banking.enums.TransactionType;
import com.ts.banking.persistence.entities.Account;
import com.ts.banking.persistence.entities.AccountHistory;
import com.ts.banking.persistence.entities.Transaction;
import com.ts.banking.persistence.repositories.AccountHistoryRepository;
import com.ts.banking.persistence.repositories.AccountRepository;
import com.ts.banking.persistence.repositories.TransactionRepository;
import com.ts.banking.services.AccountService;
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
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;


@DisplayName("Transfer Command Tests")
@ExtendWith(MockitoExtension.class)
public class WithdrawalCommandTest {

    @InjectMocks
    private WithdrawalCommand withdrawalCommand;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountHistoryRepository accountHistoryRepository;

    @Mock
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        Account source = TestingUtils.createPersonAndAccount(1L, "sourcen@gmail.com", 54321L, BigDecimal.valueOf(100));

        lenient().when(accountRepository.findById(1L)).thenReturn(Optional.of(source));

        lenient().when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());
        lenient().when(accountHistoryRepository.save(any(AccountHistory.class))).then(returnsFirstArg());

        lenient().when(accountService.getAccountBalance(1L)).thenReturn(BigDecimal.valueOf(100));
    }

    @DisplayName("Given valid transfer amount and source THEN return valid response")
    @Test
    public void whenGivenValidRequest_thenReturnResponse() {

        WithdrawalReq withdrawalReq = buildWithdrawalReq();
        WithdrawalResp resp = withdrawalCommand.execute(withdrawalReq);

        assertThat(resp.getAmount()).isEqualTo(withdrawalReq.getAmount());
        assertThat(resp.getCurrentBalance()).isEqualTo(withdrawalReq.getAmount());
        assertThat(resp.getTransactionType()).isEqualTo(TransactionType.WITHDRAWAL);
    }

    @DisplayName("Given invalid transfer amount THEN throw error")
    @Test
    public void whenGivenInvalidAmount_thenThrowError() {

        WithdrawalReq withdrawalReq = WithdrawalReq.builder()
                .amount(BigDecimal.ZERO)
                .source(1L)
                .build();

        Assertions.assertThrows(RuntimeException.class, () -> withdrawalCommand.execute(withdrawalReq));

    }

    @DisplayName("Given invalid destination THEN throw error")
    @Test
    public void whenGivenInvalidDestination_thenThrowError() {

        WithdrawalReq withdrawalReq = WithdrawalReq.builder()
                .amount(BigDecimal.TEN)
                .source(2L)
                .build();

        Assertions.assertThrows(RuntimeException.class, () -> withdrawalCommand.execute(withdrawalReq));

    }

    private static WithdrawalReq buildWithdrawalReq() {
        return WithdrawalReq.builder()
                .amount(BigDecimal.valueOf(100))
                .source(1L)
                .build();
    }
}
