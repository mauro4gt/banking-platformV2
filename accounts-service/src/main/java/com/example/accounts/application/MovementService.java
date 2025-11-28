package com.example.accounts.application;

import com.example.accounts.domain.Account;
import com.example.accounts.domain.Movement;
import com.example.accounts.infra.repository.AccountRepository;
import com.example.accounts.infra.repository.MovementRepository;
import com.example.common.exception.BusinessException;
import com.example.common.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MovementService {

    private static final Logger log = LoggerFactory.getLogger(MovementService.class);

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    public MovementService(MovementRepository movementRepository,
                           AccountRepository accountRepository) {
        this.movementRepository = movementRepository;
        this.accountRepository = accountRepository;
    }

    public Mono<Movement> registerMovement(Long accountId, BigDecimal amount, String type) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new BusinessException("Movement amount must be greater than zero"));
        }

        return Mono.fromCallable(() -> {
                    Account account = accountRepository.findById(accountId)
                            .orElseThrow(() -> new NotFoundException("Account not found: " + accountId));

                    BigDecimal currentBalance = getCurrentBalance(account);
                    BigDecimal newBalance;

                    if ("DEBIT".equalsIgnoreCase(type)) {
                        if (currentBalance.compareTo(amount) < 0) {
                            throw new BusinessException("Saldo no disponible");
                        }
                        newBalance = currentBalance.subtract(amount);
                    } else if ("CREDIT".equalsIgnoreCase(type)) {
                        newBalance = currentBalance.add(amount);
                    } else {
                        throw new BusinessException("Invalid movement type");
                    }

                    Movement movement = new Movement();
                    movement.setAccount(account);
                    movement.setDate(LocalDateTime.now());
                    movement.setType(type.toUpperCase());
                    movement.setAmount(amount);
                    movement.setBalance(newBalance);

                    log.info("Registering movement type {} for account {}: amount={} newBalance={}",
                            type, account.getNumber(), amount, newBalance);

                    return movementRepository.save(movement);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private BigDecimal getCurrentBalance(Account account) {
        Optional<Movement> last = movementRepository.findTopByAccountOrderByDateDesc(account);
        return last.map(Movement::getBalance).orElse(account.getInitialBalance());
    }
}

