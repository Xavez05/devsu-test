package com.bank.accounts.dto;

import com.bank.accounts.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private LocalDate date;
    private String type;
    private BigDecimal amount;
    private BigDecimal balance;

    public static TransactionDTO fromEntity(Transaction tx) {
        return TransactionDTO.builder()
                .date(tx.getDate())
                .type(tx.getType().name())
                .amount(tx.getAmount())
                .balance(tx.getBalance())
                .build();
    }
}
