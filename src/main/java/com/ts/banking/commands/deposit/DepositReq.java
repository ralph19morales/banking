package com.ts.banking.commands.deposit;

import com.ts.banking.commands.BaseReq;
import lombok.Builder;

import java.math.BigDecimal;

public class DepositReq extends BaseReq {

    @Builder
    public DepositReq(BigDecimal amount, Long destination) {
        super(amount, null, destination);
    }
}
