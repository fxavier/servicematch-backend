package com.xavier.servicematchbackend.geomatching.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProviderZoneId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private UUID providerId;

    @Column(name = "zone_id", nullable = false)
    private UUID zoneId;

    protected ProviderZoneId() {
    }

    public ProviderZoneId(UUID providerId, UUID zoneId) {
        this.providerId = providerId;
        this.zoneId = zoneId;
    }

    public UUID providerId() {
        return providerId;
    }

    public UUID zoneId() {
        return zoneId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProviderZoneId that = (ProviderZoneId) o;
        return Objects.equals(providerId, that.providerId) && Objects.equals(zoneId, that.zoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, zoneId);
    }
}
