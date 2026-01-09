package com.xavier.servicematchbackend.profiles.domain.entity;

import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "customer_profiles")
public class CustomerProfile {

    @EmbeddedId
    private UserId userId;

    @Column(name = "display_name", length = 120)
    private String displayName;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CustomerProfile() {
    }

    private CustomerProfile(UserId userId, Instant now) {
        this.userId = requireNonNull(userId, "userId must not be null");
        this.createdAt = requireNonNull(now, "createdAt must not be null");
        this.updatedAt = now;
    }

    public static CustomerProfile create(UserId userId, Instant now) {
        return new CustomerProfile(userId, now);
    }

    public UserId userId() {
        return userId;
    }

    public String displayName() {
        return displayName;
    }

    public String phone() {
        return phone;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public void update(String displayName, String phone, Instant now) {
        if (displayName != null) {
            this.displayName = displayName;
        }
        if (phone != null) {
            this.phone = phone;
        }
        this.updatedAt = requireNonNull(now, "updatedAt must not be null");
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
        CustomerProfile that = (CustomerProfile) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
