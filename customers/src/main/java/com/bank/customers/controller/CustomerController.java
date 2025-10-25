package com.bank.customers.controller;

import com.bank.customers.model.Customer;
import com.bank.customers.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(customer));
    }

    @GetMapping
    public List<Customer> getAll() {
        return service.findAll();
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getById(@PathVariable String customerId) {
        return ResponseEntity.ok(service.findByCustomerId(customerId));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> update(@PathVariable String customerId, @RequestBody Customer customer) {
        return ResponseEntity.ok(service.update(customerId, customer));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> delete(@PathVariable String customerId) {
        service.delete(customerId);
        return ResponseEntity.noContent().build();
    }
}
