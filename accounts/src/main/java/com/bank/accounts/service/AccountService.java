package com.bank.accounts.service;

import com.bank.accounts.exception.NotFoundException;
import com.bank.accounts.model.Account;
import com.bank.accounts.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    @Transactional
    public Account create(Account account) {
        account.setAccountNumber(UUID.randomUUID().toString());
        return repository.save(account);
    }

    public List<Account> findAll() {
        return repository.findAll();
    }

    public Account findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }
}
