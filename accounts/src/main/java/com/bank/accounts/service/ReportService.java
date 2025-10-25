package com.bank.accounts.service;

import com.bank.accounts.dto.AccountReportDTO;
import com.bank.accounts.dto.TransactionDTO;
import com.bank.accounts.model.Account;
import com.bank.accounts.model.Transaction;
import com.bank.accounts.repository.AccountRepository;
import com.bank.accounts.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;

    public List<AccountReportDTO> getReport(LocalDate from, LocalDate to, String customerId) {
        List<Account> accounts = accountRepo.findAll().stream()
                .filter(acc -> acc.getCustomerId().equals(customerId))
                .toList();

        return accounts.stream().map(acc -> {
            List<Transaction> txEntities = txRepo.findByAccount_AccountNumberAndDateBetween(
                    acc.getAccountNumber(), from, to);

            List<TransactionDTO> txList = txEntities.stream()
                    .map(TransactionDTO::fromEntity)
                    .collect(Collectors.toList());

            BigDecimal currentBalance = acc.getInitialBalance();
            for (Transaction tx : txEntities) {
                currentBalance = tx.getBalance();
            }

            if (txEntities.isEmpty()) {
                currentBalance = acc.getInitialBalance();
            }

            return AccountReportDTO.builder()
                    .accountNumber(acc.getAccountNumber())
                    .accountType(acc.getAccountType().name())
                    .initialBalance(acc.getInitialBalance())
                    .currentBalance(currentBalance)
                    .status(acc.getStatus())
                    .transactions(txList)
                    .build();
        }).toList();
    }
}
