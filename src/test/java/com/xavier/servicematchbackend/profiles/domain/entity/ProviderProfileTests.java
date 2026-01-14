package com.xavier.servicematchbackend.profiles.domain.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProviderProfileTests {

    @Test
    void updateReputationRejectsOutOfRange() {
        ProviderProfile profile = ProviderProfile.create(UserId.of(UUID.randomUUID()), Instant.now());

        assertThatThrownBy(() -> profile.updateReputation(0.5, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reputation must be between 1 and 5");

        assertThatThrownBy(() -> profile.updateReputation(5.5, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reputation must be between 1 and 5");
    }
}
