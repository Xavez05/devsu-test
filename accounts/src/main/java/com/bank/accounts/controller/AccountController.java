package com.bank.accounts.controller;

import com.bank.accounts.model.Account;
import com.bank.accounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping
    public ResponseEntity<Account> create(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(account));
    }

    @GetMapping
    public List<Account> getAll() {
        return service.findAll();
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getByNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(service.findByAccountNumber(accountNumber));
    }
}
