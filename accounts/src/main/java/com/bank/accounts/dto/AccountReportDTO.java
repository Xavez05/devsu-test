package com.bank.accounts.dto;

import com.bank.accounts.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountReportDTO {
    private String accountNumber;
    private String accountType;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private Boolean status;
    private List<TransactionDTO> transactions;

    public static AccountReportDTO fromEntity(Account account, List<TransactionDTO> transactions) {
        return AccountReportDTO.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .initialBalance(account.getInitialBalance())
                .currentBalance(account.getInitialBalance())
                .status(account.getStatus())
                .transactions(transactions)
                .build();
    }
}
