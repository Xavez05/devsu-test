package com.bank.customers.events;

import java.io.Serializable;

public record CustomerEvent(String customerId, String name, Boolean status) implements Serializable {}

