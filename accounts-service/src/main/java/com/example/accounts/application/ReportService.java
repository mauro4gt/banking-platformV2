package com.example.accounts.application;

import com.example.accounts.api.dto.AccountStatementReport;
import com.example.accounts.domain.Account;
import com.example.accounts.domain.Movement;
import com.example.accounts.infra.repository.AccountRepository;
import com.example.accounts.infra.repository.MovementRepository;
import com.example.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    public ReportService(AccountRepository accountRepository,
                         MovementRepository movementRepository) {
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
    }

    /**
     * Genera el estado de cuenta para un cliente en un rango de fechas.
     *
     * @param customerId id del cliente (el mismo que usas como customerId en Account)
     * @param startDate  fecha inicial (inclusive)
     * @param endDate    fecha final   (inclusive)
     * @return Mono<AccountStatementReport>
     */
    public Mono<AccountStatementReport> generate(String customerId,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {

        return Mono.fromCallable(() -> {

                    // 1) Obtener las cuentas del cliente
                    List<Account> accounts = accountRepository.findByCustomerId(customerId);
                    if (accounts.isEmpty()) {
                        throw new NotFoundException("No accounts found for customer: " + customerId);
                    }

                    // 2) Normalizar rango de fechas a LocalDateTime (inicio de día y fin de día)
                    LocalDateTime from = startDate.atStartOfDay();
                    // endDate inclusive → agregamos 1 día y usamos inicio de ese día como límite superior
                    LocalDateTime to = endDate.plusDays(1).atStartOfDay();

                    // 3) Construir el resumen por cuenta
                    List<AccountStatementReport.AccountSummary> summaries =
                            accounts.stream()
                                    .map(acc -> {

                                        // 3.1) Movimientos de esta cuenta dentro del rango
                                        List<Movement> movements =
                                                movementRepository.findByAccountAndDateBetween(acc, from, to);

                                        // 3.2) Mapear a MovementSummary
                                        List<AccountStatementReport.MovementSummary> items =
                                                movements.stream()
                                                        .map(m -> new AccountStatementReport.MovementSummary(
                                                                m.getDate(),
                                                                m.getType(),
                                                                m.getAmount(),
                                                                m.getBalance()
                                                        ))
                                                        .collect(Collectors.toList());

                                        // 3.3) Crear AccountSummary
                                        return new AccountStatementReport.AccountSummary(
                                                acc.getNumber(),
                                                acc.getType(),
                                                acc.getInitialBalance(),
                                                items
                                        );
                                    })
                                    .collect(Collectors.toList());

                    // 4) Armar el reporte completo
                    return new AccountStatementReport(customerId, summaries);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
