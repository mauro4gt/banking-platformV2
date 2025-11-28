package com.example.customers.application;

import com.example.common.events.CustomerEvent;
import com.example.common.exception.NotFoundException;
import com.example.customers.domain.Customer;
import com.example.customers.infra.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository repository;
    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;

    // ✅ Constructor explícito para inyección de dependencias
    public CustomerService(CustomerRepository repository,
                           KafkaTemplate<String, CustomerEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Crea un cliente en BD y publica un evento a Kafka de forma asíncrona.
     * La publicación en Kafka NO bloquea el response del endpoint.
     */
    public Mono<Customer> create(Customer customer) {
        return Mono.fromCallable(() -> {
                    log.info("Creating customer {}", customer.getIdentification());
                    return repository.save(customer);
                })
                .subscribeOn(Schedulers.boundedElastic())
                // 👇 Aquí disparamos la publicación a Kafka como efecto secundario
                .doOnSuccess(saved -> publishAsync(saved, "CUSTOMER_CREATED"));
    }

    public Mono<Customer> findById(String id) {
        return Mono.fromCallable(() ->
                        repository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Customer not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<Customer>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Publica el evento en Kafka de forma asíncrona (fire-and-forget).
     * Si Kafka está caído, se loguea el error pero NO se rompe el flujo principal.
     */
    private void publishAsync(Customer customer, String type) {
        if (kafkaTemplate == null) {
            return;
        }

        CustomerEvent event = new CustomerEvent(
                UUID.randomUUID().toString(),
                type,
                customer.getIdentification(),
                customer.getState()
        );

        try {
            kafkaTemplate.send("customer-events", event.getCustomerId(), event)
                .whenComplete((result, ex) -> {
                if (ex == null) {
                log.info("Customer event sent to Kafka: type={}, customerId={}",
                    type, event.getCustomerId());
                } else {
                log.warn("Failed to send customer event to Kafka. Continuing without publishing.", ex);
          }
    });

        } catch (Exception ex) {
            // Por seguridad, pero normalmente no debería lanzar aquí
            log.warn("Unexpected error sending event to Kafka", ex);
        }
    }
}
