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

    // POST /api/v1/accounts
    @PostMapping
    public Mono<ResponseEntity<AccountResponse>> create(@RequestBody Mono<AccountRequest> request) {
        return request
                .map(this::toDomain)
                .flatMap(service::create)
                .map(this::toResponse)
                .map(resp -> ResponseEntity.status(201).body(resp));
    }

    // GET /api/v1/accounts/{id}
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountResponse>> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    // GET /api/v1/accounts
    @GetMapping
    public Flux<AccountResponse> getAll() {
        return service.findAll()
                .flatMapMany(Flux::fromIterable)
                .map(this::toResponse);
    }

    // GET /api/v1/accounts/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public Flux<AccountResponse> getByCustomer(@PathVariable String customerId) {
        return service.findByCustomerId(customerId)
                .flatMapMany(Flux::fromIterable)
                .map(this::toResponse);
    }

    // PUT /api/v1/accounts/{id}
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AccountResponse>> update(@PathVariable Long id,
                                                        @RequestBody Mono<AccountRequest> request) {
        return request
                .map(this::toDomain)
                .flatMap(acc -> service.update(id, acc))
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    // DELETE /api/v1/accounts/{id}
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return service.delete(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

    // Mapper de DTO -> dominio
    private Account toDomain(AccountRequest dto) {
        Account account = new Account();
        account.setNumber(dto.getNumber());
        account.setType(dto.getType());
        account.setInitialBalance(dto.getInitialBalance());
        account.setState(dto.getState());
        account.setCustomerId(dto.getCustomerId());
        return account;
    }

    // Mapper de dominio -> DTO
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
