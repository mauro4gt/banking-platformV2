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

    public Mono<AccountStatementReport> generate(String customerId,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        return Mono.fromCallable(() -> {

                    List<Account> accounts = accountRepository.findByCustomerId(customerId);
                    if (accounts.isEmpty()) {
                        throw new NotFoundException("No accounts found for customer: " + customerId);
                    }

                    LocalDateTime start = startDate.atStartOfDay();
                    LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusSeconds(1);

                    List<AccountStatementReport.AccountSummary> summaries =
                            accounts.stream()
                                    .map(acc -> {
                                        List<Movement> movements =
                                                movementRepository.findByAccountAndDateBetween(acc, start, end);

                                        List<AccountStatementReport.MovementItem> items =
                                                movements.stream()
                                                        .map(m -> new AccountStatementReport.MovementItem(
                                                                m.getDate(),
                                                                m.getType(),
                                                                m.getAmount(),
                                                                m.getBalance()
                                                        ))
                                                        .collect(Collectors.toList());

                                        return new AccountStatementReport.AccountSummary(
                                                acc.getNumber(),
                                                acc.getType(),
                                                acc.getInitialBalance(),
                                                items
                                        );
                                    })
                                    .collect(Collectors.toList());

                    return new AccountStatementReport(customerId, summaries);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
