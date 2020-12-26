package com.ts.banking.commands.transfer;

import com.ts.banking.commands.BaseReq;
import lombok.Builder;

import java.math.BigDecimal;

public class TransferReq extends BaseReq {
    @Builder
    public TransferReq(BigDecimal amount, Long source, Long destination) {
        super(amount, source, destination);
    }
}
