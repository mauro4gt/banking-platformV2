package com.example.customers.domain;

import jakarta.persistence.Entity;

@Entity
public class Customer extends Person {

    private String password;
    private Boolean state;

    public Customer() {
        super();
    }

    public Customer(String identification, String name, String gender,
                    String address, String phone,
                    String password, Boolean state) {
        super(identification, name, gender, address, phone);
        this.password = password;
        this.state = state;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}
