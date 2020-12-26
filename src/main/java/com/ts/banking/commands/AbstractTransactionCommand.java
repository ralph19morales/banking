package com.ts.banking.commands;

import com.ts.banking.persistence.entities.Account;
import com.ts.banking.persistence.entities.Transaction;
import com.ts.banking.persistence.repositories.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
public abstract class AbstractTransactionCommand<I extends BaseReq, O extends BaseResp> implements ITransactionCommand<I, O> {

    protected final AccountRepository accountRepository;

    @Autowired
    protected AbstractTransactionCommand(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public O execute(I req) {
        Transaction transaction = validateAndGetTransaction(req);
        processRequest(transaction);
        return generateResponse(transaction);
    }

    protected Transaction validateAndGetTransaction(I request) {
        if (!(request.getAmount().compareTo(BigDecimal.ZERO) > 0)) {
            throw new RuntimeException("Amount should be greater than ZERO");
        }

        Account destination = request.getDestination() == null ? null :
                accountRepository.findById(request.getDestination()).orElseThrow(() -> new RuntimeException("Destination is invalid"));
        Account source = request.getSource() == null ? null :
                accountRepository.findById(request.getSource()).orElseThrow(() -> new RuntimeException("Source is invalid"));

        return Transaction.builder()
                .amount(request.getAmount())
                .destination(destination)
                .source(source)
                .build();
    }

    protected abstract void processRequest(Transaction transaction);

    protected O generateResponse(Transaction transaction) {
        O response;
        try {
            response = getResponseType().newInstance();
            response.setCreatedAt(transaction.getCreatedAt());
            response.setTransactionId(transaction.getId());
            response.setAmount(transaction.getAmount());
            response.setTransactionType(transaction.getTransactionType());

            Optional.ofNullable(transaction.getSource()).ifPresent(source -> response.setSource(source.getId()));

            Optional.ofNullable(transaction.getDestination()).ifPresent(destination -> response.setDestination(destination.getId()));

        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Cannot get instance of response", e);
            throw new RuntimeException(e);
        }

        return response;
    }
}
