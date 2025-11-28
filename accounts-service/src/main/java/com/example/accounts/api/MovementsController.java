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

    private final MovementService service;

    public MovementsController(MovementService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Movement>> create(@RequestBody Mono<MovementRequest> request) {
        return request
                .flatMap(req -> service.registerMovement(req.getAccountId(), req.getAmount(), req.getType()))
                .map(mov -> ResponseEntity.status(HttpStatus.CREATED).body(mov));
    }

    // ✅ NUEVO: listar todos SIN usar List
    @GetMapping
    public Flux<Movement> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Movement>> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return service.delete(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

    public static class MovementRequest {
        private Long accountId;
        private String type;
        private BigDecimal amount;

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
