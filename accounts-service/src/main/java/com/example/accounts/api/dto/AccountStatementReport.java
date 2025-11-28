package com.example.accounts.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AccountStatementReport {

    private String customerId;
    private List<AccountSummary> accounts;

    public AccountStatementReport() {
    }

    public AccountStatementReport(String customerId, List<AccountSummary> accounts) {
        this.customerId = customerId;
        this.accounts = accounts;
    }

    // -------- getters / setters --------

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<AccountSummary> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountSummary> accounts) {
        this.accounts = accounts;
    }

    // =========================================================
    //  AccountSummary: información por cada cuenta del cliente
    // =========================================================
    public static class AccountSummary {

        private String accountNumber;
        private String type;
        private BigDecimal initialBalance;
        private List<MovementSummary> movements;

        public AccountSummary() {
        }

        public AccountSummary(String accountNumber,
                              String type,
                              BigDecimal initialBalance,
                              List<MovementSummary> movements) {
            this.accountNumber = accountNumber;
            this.type = type;
            this.initialBalance = initialBalance;
            this.movements = movements;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public BigDecimal getInitialBalance() {
            return initialBalance;
        }

        public void setInitialBalance(BigDecimal initialBalance) {
            this.initialBalance = initialBalance;
        }

        public List<MovementSummary> getMovements() {
            return movements;
        }

        public void setMovements(List<MovementSummary> movements) {
            this.movements = movements;
        }
    }

    // =========================================================
    //  MovementSummary: detalle de movimientos por cuenta
    // =========================================================
    public static class MovementSummary {

        private LocalDateTime date;
        private String type;
        private BigDecimal amount;
        private BigDecimal balance;

        public MovementSummary() {
        }

        public MovementSummary(LocalDateTime date,
                               String type,
                               BigDecimal amount,
                               BigDecimal balance) {
            this.date = date;
            this.type = type;
            this.amount = amount;
            this.balance = balance;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }
    }
}
