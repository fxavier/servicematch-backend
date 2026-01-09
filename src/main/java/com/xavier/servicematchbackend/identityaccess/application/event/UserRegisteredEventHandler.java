package com.xavier.servicematchbackend.identityaccess.application.event;

import com.xavier.servicematchbackend.identityaccess.domain.event.UserRegistered;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventHandler {

    private final IntegrationEventPublisher integrationEventPublisher;

    public UserRegisteredEventHandler(IntegrationEventPublisher integrationEventPublisher) {
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @EventListener
    public void on(UserRegistered event) {
        UserRegisteredIntegrationEvent integrationEvent = new UserRegisteredIntegrationEvent(
                event.userId().value().toString(),
                event.email().value()
        );
        integrationEventPublisher.publish(UserRegisteredIntegrationEvent.ROUTING_KEY, integrationEvent);
    }
}
