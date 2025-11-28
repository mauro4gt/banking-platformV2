package com.example.customers.api;

import com.example.customers.application.CustomerService;
import com.example.customers.domain.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomersController {

    private final CustomerService service;

    public CustomersController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Customer>> create(@RequestBody Mono<Customer> request) {
        return request.flatMap(service::create)
                .map(c -> ResponseEntity.status(201).body(c));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Customer>> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Customer> getAll() {
        return service.findAll().flatMapMany(Flux::fromIterable);
    }
}
