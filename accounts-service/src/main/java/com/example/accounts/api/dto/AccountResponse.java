package com.example.accounts.api.dto;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String number;
    private String type;
    private BigDecimal initialBalance;
    private Boolean state;
    private String customerId;

    public AccountResponse() {
    }

    public AccountResponse(Long id, String number, String type,
                           BigDecimal initialBalance, Boolean state,
                           String customerId) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.initialBalance = initialBalance;
        this.state = state;
        this.customerId = customerId;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public Boolean getState() {
        return state;
    }

    public String getCustomerId() {
        return customerId;
    }
}
