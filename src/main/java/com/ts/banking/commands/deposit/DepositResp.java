package com.ts.banking.commands.deposit;

import com.ts.banking.commands.BaseResp;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositResp extends BaseResp {

    private BigDecimal currentBalance;
}
