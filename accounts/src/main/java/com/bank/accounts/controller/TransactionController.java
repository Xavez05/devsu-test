package com.bank.accounts.controller;

import com.bank.accounts.model.Transaction;
import com.bank.accounts.model.TransactionType;
import com.bank.accounts.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @PostMapping
    public ResponseEntity<Transaction> register(@RequestBody Map<String, Object> body) {
        String accountNumber = (String) body.get("accountNumber");
        TransactionType type = TransactionType.valueOf((String) body.get("type"));
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(accountNumber, type, amount));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getByAccount(
            @RequestParam String accountNumber,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.MIN;
        LocalDate toDate = to != null ? LocalDate.parse(to) : LocalDate.now();

        List<Transaction> transactions = service.findByAccountAndDates(accountNumber, fromDate, toDate);
        return ResponseEntity.ok(transactions);
    }
}
