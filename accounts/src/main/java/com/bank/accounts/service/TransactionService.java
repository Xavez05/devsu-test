package com.bank.accounts.service;

import com.bank.accounts.exception.InsufficientBalanceException;
import com.bank.accounts.exception.NotFoundException;
import com.bank.accounts.model.*;
import com.bank.accounts.repository.AccountRepository;
import com.bank.accounts.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;

    @Transactional
    public Transaction register(String accountNumber, TransactionType type, BigDecimal amount) {
        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        BigDecimal newBalance;
        switch (type) {
            case DEPOSIT -> newBalance = account.getInitialBalance().add(amount.abs());
            case WITHDRAWAL -> {
                if (account.getInitialBalance().compareTo(amount.abs()) < 0)
                    throw new InsufficientBalanceException();
                newBalance = account.getInitialBalance().subtract(amount.abs());
            }
            default -> throw new IllegalArgumentException("Invalid transaction type");
        }

        account.setInitialBalance(newBalance);
        Transaction tx = Transaction.builder()
                .date(LocalDate.now())
                .type(type)
                .amount(type == TransactionType.WITHDRAWAL ? amount.abs().negate() : amount.abs())
                .balance(newBalance)
                .account(account)
                .build();

        txRepo.save(tx);
        accountRepo.save(account);
        return tx;
    }

    public List<Transaction> findByAccountAndDates(String accountNumber, LocalDate from, LocalDate to) {
        return txRepo.findByAccount_AccountNumberAndDateBetween(accountNumber, from, to);
    }
}
