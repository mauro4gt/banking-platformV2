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
import java.util.List;
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

    // CREATE movimiento (crédito/débito)
    public Mono<Movement> create(Long accountId, String type, BigDecimal amount) {
        return Mono.fromCallable(() -> {
                    Account account = accountRepository.findById(accountId)
                            .orElseThrow(() -> new NotFoundException("Account not found: " + accountId));

                    BigDecimal currentBalance = getCurrentBalance(account);
                    BigDecimal newBalance = calculateNewBalance(currentBalance, type, amount);

                    Movement movement = new Movement();
                    movement.setAccount(account);
                    movement.setDate(LocalDateTime.now());
                    movement.setType(type);
                    movement.setAmount(amount);
                    movement.setBalance(newBalance);

                    log.info("Registering movement type {} for account {}: amount={} newBalance={}",
                            type, account.getNumber(), amount, newBalance);

                    return movementRepository.save(movement);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    // READ by id
    public Mono<Movement> findById(Long id) {
        return Mono.fromCallable(() ->
                        movementRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Movement not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    // READ all
    public Mono<List<Movement>> findAll() {
        return Mono.fromCallable(movementRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    // READ por cuenta
    public Mono<List<Movement>> findByAccount(Long accountId) {
        return Mono.fromCallable(() -> {
                    Account account = accountRepository.findById(accountId)
                            .orElseThrow(() -> new NotFoundException("Account not found: " + accountId));

                    // rango completo
                    return movementRepository.findByAccountAndDateBetween(
                            account,
                            LocalDateTime.MIN,
                            LocalDateTime.MAX
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    // DELETE
    public Mono<Void> delete(Long id) {
        return Mono.fromRunnable(() -> {
                    if (!movementRepository.existsById(id)) {
                        throw new NotFoundException("Movement not found: " + id);
                    }
                    movementRepository.deleteById(id);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    // ==== helpers de negocio ====

    private BigDecimal calculateNewBalance(BigDecimal current, String type, BigDecimal amount) {
        BigDecimal newBalance;
        if ("CREDIT".equalsIgnoreCase(type)) {
            newBalance = current.add(amount);
        } else if ("DEBIT".equalsIgnoreCase(type)) {
            newBalance = current.subtract(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("Saldo no disponible");
            }
        } else {
            throw new BusinessException("Tipo de movimiento no soportado: " + type);
        }
        return newBalance;
    }

    private BigDecimal getCurrentBalance(Account account) {
        Optional<Movement> last = movementRepository.findTopByAccountOrderByDateDesc(account);
        return last.map(Movement::getBalance).orElse(account.getInitialBalance());
    }
}
