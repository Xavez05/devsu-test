package com.bank.customers.service;

import com.bank.customers.events.CustomerEvent;
import com.bank.customers.model.Customer;
import com.bank.customers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final AmqpTemplate amqpTemplate;

    @Transactional
    public Customer create(Customer customer) {
        customer.setCustomerId(UUID.randomUUID().toString());
        Customer saved = repository.save(customer);

        CustomerEvent event = new CustomerEvent(saved.getCustomerId(), saved.getName(), saved.getStatus());
        amqpTemplate.convertAndSend("customers.exchange", "customer.created", event);

        return saved;
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Customer findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Transactional
    public Customer update(String customerId, Customer data) {
        Customer existing = findByCustomerId(customerId);
        existing.setName(data.getName());
        existing.setAddress(data.getAddress());
        existing.setPhoneNumber(data.getPhoneNumber());
        existing.setStatus(data.getStatus());
        return repository.save(existing);
    }

    @Transactional
    public void delete(String customerId) {
        Customer existing = findByCustomerId(customerId);
        repository.delete(existing);
    }
}

