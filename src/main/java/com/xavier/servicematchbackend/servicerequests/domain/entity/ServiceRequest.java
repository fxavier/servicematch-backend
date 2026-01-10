package com.xavier.servicematchbackend.servicerequests.domain.entity;

import com.xavier.servicematchbackend.servicerequests.domain.valueobject.ServiceRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ServiceRequestStatus status;

    @Column(name = "location_lat", nullable = false)
    private double locationLat;

    @Column(name = "location_lng", nullable = false)
    private double locationLng;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ServiceRequest() {
    }

    private ServiceRequest(UUID id,
                           UUID requesterId,
                           UUID categoryId,
                           String description,
                           ServiceRequestStatus status,
                           double locationLat,
                           double locationLng,
                           Instant now) {
        this.id = requireNonNull(id, "id must not be null");
        this.requesterId = requireNonNull(requesterId, "requesterId must not be null");
        this.categoryId = requireNonNull(categoryId, "categoryId must not be null");
        this.description = requireDescription(description);
        this.status = requireNonNull(status, "status must not be null");
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.createdAt = requireNonNull(now, "createdAt must not be null");
        this.updatedAt = now;
    }

    public static ServiceRequest create(UUID requesterId,
                                        UUID categoryId,
                                        String description,
                                        ServiceRequestStatus status,
                                        double locationLat,
                                        double locationLng,
                                        Instant now) {
        return new ServiceRequest(
                UUID.randomUUID(),
                requesterId,
                categoryId,
                description,
                status,
                locationLat,
                locationLng,
                now
        );
    }

    public UUID id() {
        return id;
    }

    public UUID requesterId() {
        return requesterId;
    }

    public UUID categoryId() {
        return categoryId;
    }

    public String description() {
        return description;
    }

    public ServiceRequestStatus status() {
        return status;
    }

    public double locationLat() {
        return locationLat;
    }

    public double locationLng() {
        return locationLng;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public void updateStatus(ServiceRequestStatus status, Instant now) {
        this.status = requireNonNull(status, "status must not be null");
        this.updatedAt = requireNonNull(now, "updatedAt must not be null");
    }

    private static String requireDescription(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        return value.trim();
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
        ServiceRequest that = (ServiceRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
