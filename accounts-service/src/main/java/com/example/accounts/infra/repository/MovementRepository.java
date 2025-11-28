package com.example.accounts.infra.repository;

import com.example.accounts.domain.Account;
import com.example.accounts.domain.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    Optional<Movement> findTopByAccountOrderByDateDesc(Account account);

    List<Movement> findByAccountAndDateBetween(Account account, LocalDateTime start, LocalDateTime end);
}
