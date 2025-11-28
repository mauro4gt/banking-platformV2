package com.example.accounts.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String number;

    private String type;

    @Column(name = "initial_balance", nullable = false)
    private BigDecimal initialBalance;

    private Boolean state;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    public Account() {
    }

    public Account(Long id, String number, String type,
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

    public void setId(Long id) {
        this.id = id;
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
