package com.xavier.servicematchbackend.geomatching.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "provider_request_matches")
public class ProviderRequestMatch {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "location_lat", nullable = false)
    private double locationLat;

    @Column(name = "location_lng", nullable = false)
    private double locationLng;

    @Column(name = "matched_at", nullable = false)
    private Instant matchedAt;

    protected ProviderRequestMatch() {
    }

    private ProviderRequestMatch(UUID id,
                                 UUID providerId,
                                 UUID requestId,
                                 UUID categoryId,
                                 String description,
                                 double locationLat,
                                 double locationLng,
                                 Instant matchedAt) {
        this.id = id;
        this.providerId = providerId;
        this.requestId = requestId;
        this.categoryId = categoryId;
        this.description = description;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.matchedAt = matchedAt;
    }

    public static ProviderRequestMatch create(UUID providerId,
                                              UUID requestId,
                                              UUID categoryId,
                                              String description,
                                              double locationLat,
                                              double locationLng,
                                              Instant matchedAt) {
        return new ProviderRequestMatch(
                UUID.randomUUID(),
                providerId,
                requestId,
                categoryId,
                description,
                locationLat,
                locationLng,
                matchedAt
        );
    }

    public UUID id() {
        return id;
    }

    public UUID providerId() {
        return providerId;
    }

    public UUID requestId() {
        return requestId;
    }

    public UUID categoryId() {
        return categoryId;
    }

    public String description() {
        return description;
    }

    public double locationLat() {
        return locationLat;
    }

    public double locationLng() {
        return locationLng;
    }

    public Instant matchedAt() {
        return matchedAt;
    }
}
