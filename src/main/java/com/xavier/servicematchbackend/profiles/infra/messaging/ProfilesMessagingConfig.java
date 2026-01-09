package com.xavier.servicematchbackend.profiles.infra.messaging;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfilesMessagingConfig {

    @Bean
    Queue userRegisteredQueue(@Value("${messaging.profiles.user-registered-queue:profiles.user-registered}") String queue) {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    TopicExchange eventsExchange(@Value("${messaging.exchange:servicematch.events}") String exchange) {
        return ExchangeBuilder.topicExchange(exchange).durable(true).build();
    }

    @Bean
    Binding userRegisteredBinding(Queue userRegisteredQueue,
                                  Exchange eventsExchange) {
        return BindingBuilder.bind(userRegisteredQueue)
                .to(eventsExchange)
                .with("user.registered.v1")
                .noargs();
    }
}
