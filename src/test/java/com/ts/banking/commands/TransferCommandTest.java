package com.ts.banking.commands;

import com.ts.banking.commands.transfer.TransferCommand;
import com.ts.banking.commands.transfer.TransferReq;
import com.ts.banking.commands.transfer.TransferResp;
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
public class TransferCommandTest {

    @InjectMocks
    private TransferCommand transferCommand;

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
        Account source = TestingUtils.createPersonAndAccount(1L, "source@gmail.com", 12345L, BigDecimal.valueOf(100));
        Account destination = TestingUtils.createPersonAndAccount(2L, "destination@gmail.com", 54321L, BigDecimal.valueOf(50));

        lenient().when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        lenient().when(accountRepository.findById(2L)).thenReturn(Optional.of(destination));

        lenient().when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());
        lenient().when(accountHistoryRepository.save(any(AccountHistory.class))).then(returnsFirstArg());

        lenient().when(accountService.getAccountBalance(1L)).thenReturn(BigDecimal.valueOf(100));
        lenient().when(accountService.getAccountBalance(2L)).thenReturn(BigDecimal.valueOf(50));
    }

    @DisplayName("Given valid transfer amount, source and destination THEN return valid response")
    @Test
    public void whenGivenValidRequest_thenReturnResponse() {

        TransferReq transferReq = buildTransferReq();
        TransferResp resp = transferCommand.execute(transferReq);

        assertThat(resp.getAmount()).isEqualTo(transferReq.getAmount());
        assertThat(resp.getTransactionType()).isEqualTo(TransactionType.TRANSFER);
    }

    @DisplayName("Given invalid transfer amount THEN throw error")
    @Test
    public void whenGivenInvalidAmount_thenThrowError() {

        TransferReq transferReq = TransferReq.builder()
                .amount(BigDecimal.ZERO)
                .source(1L)
                .destination(2L)
                .build();

        Assertions.assertThrows(RuntimeException.class, () -> transferCommand.execute(transferReq));

    }

    @DisplayName("Given invalid source THEN throw error")
    @Test
    public void whenGivenInvalidSource_thenThrowError() {

        TransferReq transferReq = TransferReq.builder()
                .amount(BigDecimal.TEN)
                .source(3L)
                .destination(2L)
                .build();

        Assertions.assertThrows(RuntimeException.class, () -> transferCommand.execute(transferReq));

    }

    @DisplayName("Given invalid destination THEN throw error")
    @Test
    public void whenGivenInvalidDestination_thenThrowError() {

        TransferReq transferReq = TransferReq.builder()
                .amount(BigDecimal.TEN)
                .source(1L)
                .destination(3L)
                .build();

        Assertions.assertThrows(RuntimeException.class, () -> transferCommand.execute(transferReq));

    }

    private static TransferReq buildTransferReq() {
        return TransferReq.builder()
                .amount(BigDecimal.TEN)
                .source(1L)
                .destination(2L)
                .build();
    }
}
