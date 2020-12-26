package com.ts.banking.commands.transfer;

import com.ts.banking.commands.AbstractTransactionCommand;
import com.ts.banking.enums.MovementType;
import com.ts.banking.enums.TransactionType;
import com.ts.banking.persistence.entities.AccountHistory;
import com.ts.banking.persistence.entities.Transaction;
import com.ts.banking.persistence.repositories.AccountHistoryRepository;
import com.ts.banking.persistence.repositories.AccountRepository;
import com.ts.banking.persistence.repositories.TransactionRepository;
import com.ts.banking.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferCommand extends AbstractTransactionCommand<TransferReq, TransferResp> {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final AccountHistoryRepository accountHistoryRepository;

    @Autowired
    public TransferCommand(TransactionRepository transactionRepository, AccountRepository accountRepository, AccountService accountService,
                           AccountHistoryRepository accountHistoryRepository) {
        super(accountRepository);
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.accountHistoryRepository = accountHistoryRepository;
    }

    @Override
    protected Transaction validateAndGetTransaction(TransferReq request) {
        Transaction transaction = super.validateAndGetTransaction(request);
        transaction.setTransactionType(TransactionType.TRANSFER);
        return transaction;
    }

    @Override
    @Transactional
    protected void processRequest(Transaction transaction) {

        transactionRepository.save(transaction);

        accountHistoryRepository.save(AccountHistory.builder()
                .account(transaction.getSource())
                .amount(transaction.getAmount())
                .type(MovementType.GOING_OUT)
                .balance(accountService
                        .getAccountBalance(transaction.getSource().getId())
                        .subtract(transaction.getAmount()))
                .transaction(transaction)
                .build());

        accountHistoryRepository.save(AccountHistory.builder()
                .account(transaction.getDestination())
                .amount(transaction.getAmount())
                .type(MovementType.COMING_IN)
                .balance(accountService
                        .getAccountBalance(transaction.getDestination().getId())
                        .add(transaction.getAmount()))
                .transaction(transaction)
                .build());
    }

    @Override
    public Class<TransferResp> getResponseType() {
        return TransferResp.class;
    }
}
