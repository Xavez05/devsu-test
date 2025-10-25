
-- ======================================================
-- Base de datos: bank_customers
-- ======================================================
CREATE DATABASE IF NOT EXISTS bank_customers CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bank_customers;

-- Tabla: customers
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    age INT CHECK (age >= 0),
    identification VARCHAR(50) NOT NULL,
    address VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20) NOT NULL
);

-- ======================================================
-- Base de datos: bank_accounts
-- ======================================================
CREATE DATABASE IF NOT EXISTS bank_accounts CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bank_accounts;

-- Tabla: accounts
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(100) NOT NULL UNIQUE,
    account_type ENUM('SAVINGS', 'CURRENT') NOT NULL,
    initial_balance DECIMAL(15,2) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    customer_id VARCHAR(100) NOT NULL
);

-- Tabla: transactions
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAWAL') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    account_id BIGINT NOT NULL,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- ======================================================
-- Notas:
-- - Cada microservicio utiliza su propia base de datos (aisladas).
-- - La relación entre clientes y cuentas se maneja mediante customer_id,
--   pero sin claves foráneas entre bases distintas (sin dependencia directa).
-- ======================================================
