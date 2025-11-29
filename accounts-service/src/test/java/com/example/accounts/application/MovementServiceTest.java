package com.example.accounts.application;

import com.example.accounts.domain.Account;
import com.example.accounts.infra.repository.AccountRepository;
import com.example.accounts.infra.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class MovementsIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MovementRepository movementRepository;

    private Long accountId;

    @BeforeEach
    void setup() {
        movementRepository.deleteAll();
        accountRepository.deleteAll();

        Account acc = new Account();
        acc.setNumber("ACC-001");
        acc.setType("SAVINGS");
        acc.setInitialBalance(new BigDecimal("2000"));
        acc.setState(true);
        acc.setCustomerId("CUST-123");

        accountId = accountRepository.save(acc).getId();
    }

    @Test
    void createMovement_debit_ok() {
        Map<String, Object> body = Map.of(
                "accountId", accountId,
                "type", "DEBIT",
                "amount", new BigDecimal("500")
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

        assertEquals(1, movementRepository.count());
    }
}
