package com.bank.accounts.service;

import com.bank.accounts.exception.InsufficientBalanceException;
import com.bank.accounts.exception.NotFoundException;
import com.bank.accounts.model.Account;
import com.bank.accounts.model.Transaction;
import com.bank.accounts.model.TransactionType;
import com.bank.accounts.repository.AccountRepository;
import com.bank.accounts.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private TransactionRepository txRepo;

    @InjectMocks
    private TransactionService service;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setAccountNumber("12345");
        account.setInitialBalance(BigDecimal.valueOf(1000));
        account.setStatus(true);

        // si tu repo devuelve el Transaction guardado, devolvemos el mismo argumento
        when(txRepo.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void deposit_updates_balance_and_persists_transaction() {
        when(accountRepo.findByAccountNumber("12345")).thenReturn(Optional.of(account));
        when(accountRepo.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction tx = service.register("12345", TransactionType.DEPOSIT, BigDecimal.valueOf(500));

        assertNotNull(tx);
        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(new BigDecimal("1500"), account.getInitialBalance());
        assertEquals(new BigDecimal("1500"), tx.getBalance());
        verify(txRepo, times(1)).save(any(Transaction.class));
        verify(accountRepo, times(1)).save(account);
    }

    @Test
    void withdrawal_with_insufficient_balance_throws() {
        when(accountRepo.findByAccountNumber("12345")).thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class,
                () -> service.register("12345", TransactionType.WITHDRAWAL, BigDecimal.valueOf(2000)));

        verify(txRepo, never()).save(any(Transaction.class));
        verify(accountRepo, never()).save(any(Account.class));
    }

    @Test
    void unknown_account_throws_NotFound() {
        when(accountRepo.findByAccountNumber("999")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.register("999", TransactionType.DEPOSIT, BigDecimal.TEN));

        assertEquals("Account not found", ex.getMessage());
        verify(txRepo, never()).save(any(Transaction.class));
        verify(accountRepo, never()).save(any(Account.class));
    }
}
