package com.bank.accounts.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal balance;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;
}
