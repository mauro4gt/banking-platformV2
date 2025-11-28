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

    // POST /api/v1/customers
    @PostMapping
    public Mono<ResponseEntity<Customer>> create(@RequestBody Mono<Customer> request) {
        return request
                .flatMap(service::create)
                .map(c -> ResponseEntity.status(201).body(c));
    }

    // PUT /api/v1/customers/{id}
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Customer>> update(@PathVariable String id,
                                                 @RequestBody Mono<Customer> request) {
        return request
                .flatMap(c -> service.update(id, c))
                .map(ResponseEntity::ok);
    }

    // DELETE /api/v1/customers/{id}
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

    // GET /api/v1/customers/{id}
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Customer>> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok);
    }

    // GET /api/v1/customers
    @GetMapping
    public Flux<Customer> getAll() {
        return service.findAll()
                .flatMapMany(Flux::fromIterable);
    }
}
