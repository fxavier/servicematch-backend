package com.xavier.servicematchbackend.servicerequests.domain.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.xavier.servicematchbackend.servicerequests.domain.valueobject.ServiceRequestStatus;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ServiceRequestTests {

    @Test
    void createRejectsBlankDescription() {
        Instant now = Instant.now();

        assertThatThrownBy(() -> ServiceRequest.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                " ",
                ServiceRequestStatus.PUBLISHED,
                -25.9,
                32.5,
                now
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("description must not be blank");
    }

    @Test
    void updateStatusRejectsNull() {
        ServiceRequest request = ServiceRequest.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Preciso de limpeza",
                ServiceRequestStatus.PUBLISHED,
                -25.9,
                32.5,
                Instant.now()
        );

        assertThatThrownBy(() -> request.updateStatus(null, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("status must not be null");
    }
}
