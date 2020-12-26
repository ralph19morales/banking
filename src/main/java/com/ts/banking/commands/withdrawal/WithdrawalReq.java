package com.ts.banking.commands.withdrawal;

import com.ts.banking.commands.BaseReq;
import lombok.Builder;

import java.math.BigDecimal;

public class WithdrawalReq extends BaseReq {

    @Builder
    public WithdrawalReq(BigDecimal amount, Long source) {
        super(amount, source, null);
    }
}
