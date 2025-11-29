package com.example.accounts.api;

import com.example.accounts.application.MovementService;
import com.example.accounts.domain.Account;
import com.example.accounts.domain.Movement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

class MovementsIntegrationTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Creamos un Movement "dummy" que siempre se devolverá
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("500");

        Movement movement = new Movement();
        movement.setId(10L);
        movement.setDate(LocalDateTime.now());
        movement.setType("DEBIT");
        movement.setAmount(amount);
        movement.setBalance(new BigDecimal("1500"));
        Account account = new Account();
        account.setId(accountId);
        movement.setAccount(account);

        // Creamos un MovementService “fake” como subclase anónima
        MovementService fakeService = new MovementService(null, null) {
            @Override
            public Mono<Movement> create(Long accId, String type, BigDecimal amt) {
                // Aquí podrías validar los argumentos si quieres
                return Mono.just(movement);
            }
        };

        // Inyectamos este fake en el controller real
        MovementsController controller = new MovementsController(fakeService);
        this.webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void createMovement_debit_ok() {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("500");

        Map<String, Object> body = Map.of(
                "accountId", accountId,
                "type", "DEBIT",
                "amount", amount
        );

        webTestClient.post()
                .uri("/api/v1/movements")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.amount").isEqualTo(500)
                .jsonPath("$.balance").isEqualTo(1500)
                .jsonPath("$.type").isEqualTo("DEBIT");
    }
}
