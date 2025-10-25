package com.bank.accounts.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient balance");
    }
}
