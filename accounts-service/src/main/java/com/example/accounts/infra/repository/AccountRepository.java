package com.example.accounts.infra.repository;

import com.example.accounts.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCustomerId(String customerId);
}
