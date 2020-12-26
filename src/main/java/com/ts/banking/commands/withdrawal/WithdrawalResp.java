package com.ts.banking.commands.withdrawal;

import com.ts.banking.commands.BaseResp;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawalResp extends BaseResp {

    private BigDecimal currentBalance;
}
