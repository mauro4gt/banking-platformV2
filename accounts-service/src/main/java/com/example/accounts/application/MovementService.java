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
import reactor.core.publisher.Flux;
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

    // =========================================================
    // Métodos esperados por MovementsController
    // =========================================================

    /**
     * Método que usa el controller en el POST.
     * Delegamos en registerMovement para centralizar la lógica.
     */
    public Mono<Movement> create(Long accountId, String type, BigDecimal amount) {
        return registerMovement(accountId, amount, type);
    }

    /**
     * GET /api/v1/movements?accountId=...
     */
    public Flux<Movement> findByAccount(Long accountId) {
        return Mono.fromCallable(() -> {
                    Account account = accountRepository.findById(accountId)
                            .orElseThrow(() -> new NotFoundException("Account not found: " + accountId));

                    // Usamos todo el rango de fechas
                    List<Movement> list = movementRepository.findByAccountAndDateBetween(
                            account,
                            LocalDateTime.MIN,
                            LocalDateTime.MAX
                    );
                    return list;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    /**
     * GET /api/v1/movements  (sin filtros)
     */
    public Flux<Movement> findAll() {
        return Mono.fromCallable(movementRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    // =========================================================
    // Lógica de negocio principal (F2 / F3)
    // =========================================================

    /**
     * Lógica central de registro de movimiento (débito/crédito).
     * Este método lo usan:
     * - create(...) (controller)
     * - tests unitarios (MovementServiceTest)
     */
    public Mono<Movement> registerMovement(Long accountId, BigDecimal amount, String type) {
        return Mono.fromCallable(() -> {
                    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException("Movement amount must be greater than zero");
                    }

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

    // =========================================================
    // Otros métodos de CRUD que probablemente usa tu código
    // =========================================================

    public Mono<Movement> findById(Long id) {
        return Mono.fromCallable(() ->
                        movementRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Movement not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

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

    // =========================================================
    // Helpers internos
    // =========================================================

    private BigDecimal getCurrentBalance(Account account) {
        Optional<Movement> last = movementRepository.findTopByAccountOrderByDateDesc(account);
        return last.map(Movement::getBalance)
                   .orElse(account.getInitialBalance());
    }
}
