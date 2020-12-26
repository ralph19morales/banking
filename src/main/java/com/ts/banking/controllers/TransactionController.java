package com.ts.banking.controllers;

import com.ts.banking.commands.deposit.DepositCommand;
import com.ts.banking.commands.deposit.DepositReq;
import com.ts.banking.commands.transfer.TransferCommand;
import com.ts.banking.commands.transfer.TransferReq;
import com.ts.banking.commands.withdrawal.WithdrawalCommand;
import com.ts.banking.commands.withdrawal.WithdrawalReq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransferCommand transferCommand;
    private final WithdrawalCommand withdrawalCommand;
    private final DepositCommand depositCommand;

    @PostMapping("/transfer")
    public ResponseWrapper makeTransfer(@RequestBody TransferReq transferReq) {
        return ResponseWrapper.builder().data(transferCommand.execute(transferReq)).build();
    }

    @PostMapping("/withdrawal")
    public ResponseWrapper makeWithdrawal(@RequestBody WithdrawalReq withdrawalReq) {
        return ResponseWrapper.builder().data(withdrawalCommand.execute(withdrawalReq)).build();
    }

    @PostMapping("/deposit")
    public ResponseWrapper makeDeposit(@RequestBody DepositReq depositReq) {
        return ResponseWrapper.builder().data(depositCommand.execute(depositReq)).build();
    }

}
