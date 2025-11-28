package com.example.accounts.api;

import com.example.accounts.api.dto.AccountRequest;
import com.example.accounts.api.dto.AccountResponse;
import com.example.accounts.application.AccountService;
import com.example.accounts.domain.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountsController {

    private final AccountService service;

    public AccountsController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<AccountResponse>> create(@RequestBody Mono<AccountRequest> request) {
        return request
                .map(this::toDomain)
                .flatMap(service::create)
                .map(this::toResponse)
                .map(resp -> ResponseEntity.status(201).body(resp));
    }

    @GetMapping
    public Flux<AccountResponse> getAll() {
        return service.findAll()
                .flatMapMany(Flux::fromIterable)
                .map(this::toResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountResponse>> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    // 🔹 NUEVO: actualizar cuenta
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AccountResponse>> update(@PathVariable Long id,
                                                        @RequestBody Mono<AccountRequest> request) {
        return request
                .map(this::toDomain)
                .flatMap(account -> service.update(id, account))
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    // 🔹 NUEVO: eliminar cuenta
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return service.delete(id)
                .thenReturn(ResponseEntity.noContent().build());
    }



    // ------------ mapping helpers ------------

    private Account toDomain(AccountRequest req) {
        Account account = new Account();
        account.setNumber(req.getNumber());
        account.setType(req.getType());
        account.setInitialBalance(req.getInitialBalance());
        account.setState(req.getState());
        account.setCustomerId(req.getCustomerId());
        return account;
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getNumber(),
                account.getType(),
                account.getInitialBalance(),
                account.getState(),
                account.getCustomerId()
        );
    }
}
