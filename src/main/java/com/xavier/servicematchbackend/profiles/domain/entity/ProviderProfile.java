package com.xavier.servicematchbackend.profiles.domain.entity;

import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import com.xavier.servicematchbackend.profiles.domain.valueobject.ProviderZone;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "provider_profiles")
public class ProviderProfile {

    @EmbeddedId
    private UserId userId;

    @Column(name = "display_name", length = 120)
    private String displayName;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "reputation")
    private Double reputation;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_zones", joinColumns = @JoinColumn(name = "user_id"))
    private Set<ProviderZone> zones = new HashSet<>();

    protected ProviderProfile() {
    }

    private ProviderProfile(UserId userId, Instant now) {
        this.userId = requireNonNull(userId, "userId must not be null");
        this.createdAt = requireNonNull(now, "createdAt must not be null");
        this.updatedAt = now;
    }

    public static ProviderProfile create(UserId userId, Instant now) {
        return new ProviderProfile(userId, now);
    }

    public UserId userId() {
        return userId;
    }

    public String displayName() {
        return displayName;
    }

    public String bio() {
        return bio;
    }

    public Double reputation() {
        return reputation;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Set<ProviderZone> zones() {
        return Collections.unmodifiableSet(zones);
    }

    public void update(String displayName, String bio, Instant now) {
        if (displayName != null) {
            this.displayName = displayName;
        }
        if (bio != null) {
            this.bio = bio;
        }
        this.updatedAt = requireNonNull(now, "updatedAt must not be null");
    }

    public void updateReputation(Double reputation, Instant now) {
        this.reputation = normalizeReputation(reputation);
        this.updatedAt = requireNonNull(now, "updatedAt must not be null");
    }

    public ProviderZone addZone(double centerLat, double centerLng, double radiusKm, Instant now) {
        ProviderZone zone = ProviderZone.create(centerLat, centerLng, radiusKm);
        zones.add(zone);
        this.updatedAt = requireNonNull(now, "updatedAt must not be null");
        return zone;
    }

    public ProviderZone updateZone(UUID zoneId, double centerLat, double centerLng, double radiusKm, Instant now) {
        ProviderZone existing = findZone(zoneId);
        zones.remove(existing);
        ProviderZone updated = ProviderZone.of(zoneId, centerLat, centerLng, radiusKm);
        zones.add(updated);
        this.updatedAt = requireNonNull(now, "updatedAt must not be null");
        return updated;
    }

    public void removeZone(UUID zoneId, Instant now) {
        ProviderZone existing = findZone(zoneId);
        zones.remove(existing);
        this.updatedAt = requireNonNull(now, "updatedAt must not be null");
    }

    private ProviderZone findZone(UUID zoneId) {
        if (zoneId == null) {
            throw new IllegalArgumentException("zoneId must not be null");
        }
        return zones.stream()
                .filter(zone -> zone.id().equals(zoneId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("zone not found"));
    }

    private static Double normalizeReputation(Double reputation) {
        if (reputation == null) {
            return null;
        }
        if (reputation < 1.0 || reputation > 5.0) {
            throw new IllegalArgumentException("reputation must be between 1 and 5");
        }
        return reputation;
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
        ProviderProfile that = (ProviderProfile) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
