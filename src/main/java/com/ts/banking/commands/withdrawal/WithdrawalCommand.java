package com.ts.banking.commands.withdrawal;

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
public class WithdrawalCommand extends AbstractTransactionCommand<WithdrawalReq, WithdrawalResp> {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final AccountHistoryRepository accountHistoryRepository;

    @Autowired
    public WithdrawalCommand(TransactionRepository transactionRepository, AccountRepository accountRepository, AccountService accountService,
                             AccountHistoryRepository accountHistoryRepository) {
        super(accountRepository);
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.accountHistoryRepository = accountHistoryRepository;
    }

    @Override
    protected Transaction validateAndGetTransaction(WithdrawalReq request) {
        Transaction transaction = super.validateAndGetTransaction(request);

        if(request.getSource() == null) {
            throw new RuntimeException("Source is missing");
        }

        transaction.setTransactionType(TransactionType.WITHDRAWAL);
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

    }

    @Override
    protected WithdrawalResp generateResponse(Transaction transaction) {
        WithdrawalResp withdrawalResp = super.generateResponse(transaction);
        withdrawalResp.setCurrentBalance(transaction.getSource().getHistory()
                .get(transaction.getSource().getHistory().size() - 1).getBalance());
        return withdrawalResp;
    }

    @Override
    public Class<WithdrawalResp> getResponseType() {
        return WithdrawalResp.class;
    }
}
