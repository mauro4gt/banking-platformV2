package com.example.accounts.api;

import com.example.accounts.application.MovementService;
import com.example.accounts.domain.Movement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
                .flatMap(req ->
                        service.registerMovement(req.getAccountId(), req.getAmount(), req.getType())
                )
                .map(movement -> ResponseEntity.status(201).body(movement));
    }

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
