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

    // CREATE
    public Mono<Account> create(Account account) {
        return Mono.fromCallable(() -> repository.save(account))
                .subscribeOn(Schedulers.boundedElastic());
    }

    // UPDATE
    public Mono<Account> update(Long id, Account data) {
        return Mono.fromCallable(() -> {
                    return repository.findById(id)
                            .map(existing -> {
                                existing.setNumber(data.getNumber());
                                existing.setType(data.getType());
                                existing.setInitialBalance(data.getInitialBalance());
                                existing.setState(data.getState());
                                existing.setCustomerId(data.getCustomerId());
                                return repository.save(existing);
                            })
                            .orElseThrow(() -> new NotFoundException("Account not found: " + id));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    // READ by id
    public Mono<Account> findById(Long id) {
        return Mono.fromCallable(() ->
                        repository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Account not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    // READ all
    public Mono<List<Account>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    // READ by customerId
    public Mono<List<Account>> findByCustomerId(String customerId) {
        return Mono.fromCallable(() -> repository.findByCustomerId(customerId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    // DELETE
    public Mono<Void> delete(Long id) {
        return Mono.fromRunnable(() -> {
                    if (!repository.existsById(id)) {
                        throw new NotFoundException("Account not found: " + id);
                    }
                    repository.deleteById(id);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
