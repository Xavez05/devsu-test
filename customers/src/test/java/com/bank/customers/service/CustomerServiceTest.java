package com.bank.customers.service;

import com.bank.customers.events.CustomerEvent;
import com.bank.customers.model.Customer;
import com.bank.customers.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private AmqpTemplate amqpTemplate;

    @InjectMocks
    private CustomerService service;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("Jose Lema");
        customer.setStatus(true);

        when(repository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setCustomerId(UUID.randomUUID().toString());
            return c;
        });
    }

    @Test
    void create_shouldSaveCustomerAndPublishEvent() {
        Customer saved = service.create(customer);

        assertNotNull(saved.getCustomerId());
        assertEquals("Jose Lema", saved.getName());
        verify(repository, times(1)).save(any(Customer.class));

        verify(amqpTemplate, times(1))
                .convertAndSend(eq("customers.exchange"), eq("customer.created"), any(CustomerEvent.class));
    }
}
