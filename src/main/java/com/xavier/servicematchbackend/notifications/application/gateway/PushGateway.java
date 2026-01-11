package com.xavier.servicematchbackend.notifications.application.gateway;

import com.xavier.servicematchbackend.notifications.application.dto.PushMessage;

public interface PushGateway {

    void send(PushMessage message);
}
