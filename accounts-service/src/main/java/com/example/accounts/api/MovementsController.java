package com.example.accounts.api;

import com.example.accounts.application.MovementService;
import com.example.accounts.domain.Movement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/movements")
public class MovementsController {

    private final MovementService movementService;

    public MovementsController(MovementService movementService) {
        this.movementService = movementService;
    }

    // ================== POST ==================
    @PostMapping
    public Mono<ResponseEntity<Movement>> create(@RequestBody Mono<MovementRequest> request) {
        return request
                .flatMap(r -> movementService.create(r.getAccountId(), r.getType(), r.getAmount()))
                .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(m));
    }

    // ================== GET BY ID ==================
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Movement>> getById(@PathVariable Long id) {
        return movementService.findById(id)
                .map(ResponseEntity::ok);
    }

    // ================== GET ALL / BY ACCOUNT ==================
    @GetMapping
    public Flux<Movement> getAll(@RequestParam(value = "accountId", required = false) Long accountId) {
        if (accountId != null) {
            // ahora movementService.findByAccount devuelve Flux<Movement>
            return movementService.findByAccount(accountId);
        }
        // ahora movementService.findAll devuelve Flux<Movement>
        return movementService.findAll();
    }

    // ================== DELETE ==================
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return movementService.delete(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

    // ================== DTO de request ==================
    public static class MovementRequest {

        private Long accountId;
        private String type;
        private BigDecimal amount;

        public MovementRequest() {
        }

        public Long getAccountId() {
            return accountId;
        }

        public void setAccountId(Long accountId) {
            this.accountId = accountId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}
