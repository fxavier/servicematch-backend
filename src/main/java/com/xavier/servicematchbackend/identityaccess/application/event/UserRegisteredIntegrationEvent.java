package com.xavier.servicematchbackend.identityaccess.application.event;

import com.xavier.servicematchbackend.common.domain.event.IntegrationEvent;

public class UserRegisteredIntegrationEvent extends IntegrationEvent {

    public static final String ROUTING_KEY = "user.registered.v1";

    private final String userId;
    private final String email;

    public UserRegisteredIntegrationEvent(String userId, String email) {
        super("v1");
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
