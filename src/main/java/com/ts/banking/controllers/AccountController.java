package com.ts.banking.controllers;

import com.ts.banking.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;


    @PostMapping("create")
    public ResponseWrapper createAccount(@RequestBody AccountService.CreateAccountReq req) {
        return ResponseWrapper.builder().data(accountService.createAccount(req)).build();
    }

    @GetMapping("/{accountId}")
    public ResponseWrapper getAccountSummary(@PathVariable Long accountId) {
        return ResponseWrapper.builder().data(accountService.getAccountSummary(accountId)).build();
    }
}
