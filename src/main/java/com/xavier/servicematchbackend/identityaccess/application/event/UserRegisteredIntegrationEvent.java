package com.xavier.servicematchbackend.identityaccess.application.event;

import com.xavier.servicematchbackend.common.domain.event.IntegrationEvent;
import java.util.List;

public class UserRegisteredIntegrationEvent extends IntegrationEvent {

    public static final String ROUTING_KEY = "user.registered.v1";

    private final String userId;
    private final String email;
    private final List<String> roles;

    public UserRegisteredIntegrationEvent(String userId, String email, List<String> roles) {
        super("v1");
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("roles must not be empty");
        }
        if (roles.contains(null)) {
            throw new IllegalArgumentException("roles must not contain nulls");
        }
        this.userId = userId;
        this.email = email;
        this.roles = List.copyOf(roles);
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}
