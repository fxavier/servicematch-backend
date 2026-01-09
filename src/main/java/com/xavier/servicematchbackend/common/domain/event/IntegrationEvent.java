package com.xavier.servicematchbackend.common.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for integration events published outside the service boundary.
 *
 * <p>
 * These events are designed to be consumed by other services
 * via messaging systems such as RabbitMQ.
 * </p>
 *
 * <p>
 * Integration events MUST be versioned to preserve backward compatibility.
 * </p>
 */
public abstract class IntegrationEvent {

    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String version;

    /**
     * Creates a new integration event.
     *
     * @param version semantic version of the event contract (e.g. v1, v2)
     */
    protected IntegrationEvent(String version) {
        this.version = version;
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getVersion() {
        return version;
    }
}
