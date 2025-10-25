package com.bank.accounts.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomerEventListener {

    private final Map<String, CustomerEvent> customerCache = new ConcurrentHashMap<>();

    @RabbitListener(queues = "customers.q")
    public void onCustomerEvent(CustomerEvent event) {
        log.info("Received CustomerEvent: {}", event);
        customerCache.put(event.customerId(), event);
    }

    public boolean exists(String customerId) {
        return customerCache.containsKey(customerId);
    }
}
