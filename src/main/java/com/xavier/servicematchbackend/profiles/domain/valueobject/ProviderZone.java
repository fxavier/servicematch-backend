package com.xavier.servicematchbackend.profiles.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProviderZone {

    public static final double MAX_RADIUS_KM = 50.0;

    @Column(name = "zone_id", nullable = false)
    private UUID id;

    @Column(name = "center_lat", nullable = false)
    private double centerLat;

    @Column(name = "center_lng", nullable = false)
    private double centerLng;

    @Column(name = "radius_km", nullable = false)
    private double radiusKm;

    protected ProviderZone() {
    }

    private ProviderZone(UUID id, double centerLat, double centerLng, double radiusKm) {
        this.id = requireNonNull(id, "zoneId must not be null");
        this.centerLat = validateLatitude(centerLat);
        this.centerLng = validateLongitude(centerLng);
        this.radiusKm = validateRadius(radiusKm);
    }

    public static ProviderZone create(double centerLat, double centerLng, double radiusKm) {
        return new ProviderZone(UUID.randomUUID(), centerLat, centerLng, radiusKm);
    }

    public static ProviderZone of(UUID id, double centerLat, double centerLng, double radiusKm) {
        return new ProviderZone(id, centerLat, centerLng, radiusKm);
    }

    public UUID id() {
        return id;
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

    private static double validateLatitude(double value) {
        if (value < -90.0 || value > 90.0) {
            throw new IllegalArgumentException("centerLat must be between -90 and 90");
        }
        return value;
    }

    private static double validateLongitude(double value) {
        if (value < -180.0 || value > 180.0) {
            throw new IllegalArgumentException("centerLng must be between -180 and 180");
        }
        return value;
    }

    private static double validateRadius(double value) {
        if (value <= 0.0) {
            throw new IllegalArgumentException("radiusKm must be positive");
        }
        if (value > MAX_RADIUS_KM) {
            throw new IllegalArgumentException("radiusKm must be <= " + MAX_RADIUS_KM);
        }
        return value;
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProviderZone that = (ProviderZone) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
