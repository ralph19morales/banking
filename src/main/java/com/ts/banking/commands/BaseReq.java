package com.ts.banking.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BaseReq {

    private BigDecimal amount;

    private Long source;

    private Long destination;

}
