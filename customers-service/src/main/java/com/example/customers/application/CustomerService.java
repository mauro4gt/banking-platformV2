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
    private static final String TOPIC = "customers-events";

    private final CustomerRepository repository;
    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;

    public CustomerService(CustomerRepository repository,
                           KafkaTemplate<String, CustomerEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // CREATE
    public Mono<Customer> create(Customer customer) {
        return Mono.fromCallable(() -> repository.save(customer))
                .doOnSuccess(c -> sendEvent("CUSTOMER_CREATED", c))
                .subscribeOn(Schedulers.boundedElastic());
    }

    // UPDATE
    public Mono<Customer> update(String id, Customer data) {
        return Mono.fromCallable(() -> {
                    Customer existing = repository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Customer not found: " + id));

                    existing.setName(data.getName());
                    existing.setGender(data.getGender());
                    existing.setAddress(data.getAddress());
                    existing.setPhone(data.getPhone());
                    existing.setPassword(data.getPassword());
                    existing.setState(data.getState());

                    Customer saved = repository.save(existing);
                    sendEvent("CUSTOMER_UPDATED", saved);
                    return saved;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    // DELETE (lógico o físico, según tu modelo; aquí físico)
    public Mono<Void> delete(String id) {
        return Mono.fromRunnable(() -> {
                    Customer existing = repository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
                    repository.delete(existing);
                    sendEvent("CUSTOMER_DELETED", existing);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    // READ by id
    public Mono<Customer> findById(String id) {
        return Mono.fromCallable(() ->
                        repository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Customer not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    // READ all
    public Mono<List<Customer>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    // Envío de evento a Kafka
    private void sendEvent(String type, Customer customer) {
        try {
            CustomerEvent event = new CustomerEvent(
                    UUID.randomUUID().toString(),
                    type,
                    customer.getIdentification(),
                    customer.getState()
            );

            kafkaTemplate.send(TOPIC, event.getCustomerId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Customer event sent to Kafka: type={}, customerId={}",
                                    type, event.getCustomerId());
                        } else {
                            log.warn("Failed to send customer event to Kafka. Continuing without publishing.", ex);
                        }
                    });

        } catch (Exception ex) {
            log.warn("Unexpected error sending event to Kafka", ex);
        }
    }
}
