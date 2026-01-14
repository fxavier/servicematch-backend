package com.xavier.servicematchbackend.notifications.infra.gateway;

import com.xavier.servicematchbackend.notifications.application.dto.PushMessage;
import com.xavier.servicematchbackend.notifications.application.gateway.PushGateway;
import org.springframework.stereotype.Component;

@Component
public class StubPushGateway implements PushGateway {

    @Override
    public void send(PushMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
    }
}
