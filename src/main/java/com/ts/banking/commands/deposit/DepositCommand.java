package com.ts.banking.commands.deposit;

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
public class DepositCommand extends AbstractTransactionCommand<DepositReq, DepositResp> {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final AccountHistoryRepository accountHistoryRepository;

    @Autowired
    public DepositCommand(TransactionRepository transactionRepository, AccountRepository accountRepository, AccountService accountService,
                          AccountHistoryRepository accountHistoryRepository) {
        super(accountRepository);
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.accountHistoryRepository = accountHistoryRepository;
    }

    @Override
    protected Transaction validateAndGetTransaction(DepositReq request) {
        Transaction transaction = super.validateAndGetTransaction(request);

        if(request.getDestination() == null) {
            throw new RuntimeException("Destination is missing");
        }

        transaction.setTransactionType(TransactionType.DEPOSIT);
        return transaction;
    }

    @Override
    protected void processRequest(Transaction transaction) {

        transactionRepository.save(transaction);

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
    protected DepositResp generateResponse(Transaction transaction) {
        DepositResp depositResp = super.generateResponse(transaction);
        depositResp.setCurrentBalance(transaction.getDestination().getHistory()
                .get(transaction.getDestination().getHistory().size() - 1).getBalance());
        return depositResp;
    }

    @Override
    public Class<DepositResp> getResponseType() {
        return DepositResp.class;
    }
}
