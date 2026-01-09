package com.xavier.servicematchbackend.identityaccess.infra.messaging;

import com.xavier.servicematchbackend.common.domain.event.IntegrationEvent;
import com.xavier.servicematchbackend.identityaccess.application.event.IntegrationEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqIntegrationEventPublisher implements IntegrationEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;

    public RabbitMqIntegrationEventPublisher(RabbitTemplate rabbitTemplate,
                                             MessagingProperties messagingProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.messagingProperties = messagingProperties;
    }

    @Override
    public void publish(String routingKey, IntegrationEvent event) {
        rabbitTemplate.convertAndSend(messagingProperties.getExchange(), routingKey, event);
    }
}
