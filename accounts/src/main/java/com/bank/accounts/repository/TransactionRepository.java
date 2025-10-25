package com.bank.accounts.repository;

import com.bank.accounts.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_AccountNumberAndDateBetween(String accountNumber, LocalDate from, LocalDate to);

}
