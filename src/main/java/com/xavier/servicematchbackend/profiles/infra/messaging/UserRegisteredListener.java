package com.xavier.servicematchbackend.profiles.infra.messaging;

import com.xavier.servicematchbackend.profiles.application.usecase.ProfilesBootstrapService;
import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredListener {

    private final ProfilesBootstrapService profilesBootstrapService;

    public UserRegisteredListener(ProfilesBootstrapService profilesBootstrapService) {
        this.profilesBootstrapService = profilesBootstrapService;
    }

    @RabbitListener(queues = "${messaging.profiles.user-registered-queue:profiles.user-registered}")
    public void handle(UserRegisteredMessage message) {
        if (message == null) {
            return;
        }
        if (message.version() != null && !message.version().equals("v1")) {
            return;
        }
        UserId userId = UserId.fromString(message.userId());
        Set<String> roles = toRoleSet(message.roles());
        profilesBootstrapService.bootstrap(userId, roles);
    }

    private Set<String> toRoleSet(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(roles);
    }
}
