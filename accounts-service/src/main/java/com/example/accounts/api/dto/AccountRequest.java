package com.example.accounts.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;

public class AccountRequest {

    @JsonAlias({"number", "accountNumber"})
    private String number;

    private String type;

    private BigDecimal initialBalance;

    @JsonAlias({"state", "status"})
    private Boolean state;

    private String customerId;

    public AccountRequest() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
