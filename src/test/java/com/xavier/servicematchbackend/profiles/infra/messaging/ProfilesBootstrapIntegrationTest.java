package com.xavier.servicematchbackend.profiles.infra.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import com.xavier.servicematchbackend.profiles.infra.persistence.CustomerProfileRepository;
import com.xavier.servicematchbackend.profiles.infra.persistence.ProviderProfileRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProfilesBootstrapIntegrationTest {

    @Autowired
    private UserRegisteredListener listener;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private ProviderProfileRepository providerProfileRepository;

    @Test
    void createsProfilesIdempotentlyByRole() {
        UserId userId = UserId.of(UUID.randomUUID());
        UserRegisteredMessage message = new UserRegisteredMessage(
                userId.value().toString(),
                "user@example.com",
                List.of("CLIENT", "PROVIDER"),
                "v1"
        );

        listener.handle(message);
        listener.handle(message);

        assertThat(customerProfileRepository.findById(userId)).isPresent();
        assertThat(providerProfileRepository.findById(userId)).isPresent();
    }
}
