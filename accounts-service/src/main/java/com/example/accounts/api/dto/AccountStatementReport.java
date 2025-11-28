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

    public String getCustomerId() {
        return customerId;
    }

    public List<AccountSummary> getAccounts() {
        return accounts;
    }

    // -------- inner DTOs --------

    public static class AccountSummary {
        private String accountNumber;
        private String type;
        private BigDecimal initialBalance;
        private List<MovementItem> movements;

        public AccountSummary() {
        }

        public AccountSummary(String accountNumber, String type,
                              BigDecimal initialBalance, List<MovementItem> movements) {
            this.accountNumber = accountNumber;
            this.type = type;
            this.initialBalance = initialBalance;
            this.movements = movements;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getType() {
            return type;
        }

        public BigDecimal getInitialBalance() {
            return initialBalance;
        }

        public List<MovementItem> getMovements() {
            return movements;
        }
    }

    public static class MovementItem {
        private LocalDateTime date;
        private String type;
        private BigDecimal amount;
        private BigDecimal balance;

        public MovementItem() {
        }

        public MovementItem(LocalDateTime date, String type,
                            BigDecimal amount, BigDecimal balance) {
            this.date = date;
            this.type = type;
            this.amount = amount;
            this.balance = balance;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public String getType() {
            return type;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public BigDecimal getBalance() {
            return balance;
        }
    }
}
