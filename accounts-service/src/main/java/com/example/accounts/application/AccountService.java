package com.example.accounts.application;

import com.example.accounts.domain.Account;
import com.example.accounts.infra.repository.AccountRepository;
import com.example.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Mono<Account> create(Account account) {
        return Mono.fromCallable(() -> repository.save(account))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Account> findById(Long id) {
        return Mono.fromCallable(() ->
                        repository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Account not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<Account>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
