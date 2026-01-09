package com.xavier.servicematchbackend.identityaccess.application.event;

import com.xavier.servicematchbackend.common.domain.event.IntegrationEvent;

public interface IntegrationEventPublisher {

    void publish(String routingKey, IntegrationEvent event);
}
