package com.bank.accounts.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "customers.exchange";
    public static final String QUEUE = "customers.q";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("customer.*");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
