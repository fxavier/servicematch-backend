package com.xavier.servicematchbackend.geomatching.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "provider_zones")
public class ProviderZone {

    @EmbeddedId
    private ProviderZoneId id;

    @Column(name = "center_lat", nullable = false)
    private double centerLat;

    @Column(name = "center_lng", nullable = false)
    private double centerLng;

    @Column(name = "radius_km", nullable = false)
    private double radiusKm;

    protected ProviderZone() {
    }

    public ProviderZone(ProviderZoneId id, double centerLat, double centerLng, double radiusKm) {
        this.id = id;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.radiusKm = radiusKm;
    }

    public UUID providerId() {
        return id != null ? id.providerId() : null;
    }

    public UUID zoneId() {
        return id != null ? id.zoneId() : null;
    }

    public double centerLat() {
        return centerLat;
    }

    public double centerLng() {
        return centerLng;
    }

    public double radiusKm() {
        return radiusKm;
    }
}
