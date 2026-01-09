package com.xavier.servicematchbackend.profiles.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserId {

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID value;

    protected UserId() {
    }

    private UserId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        this.value = value;
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        return new UserId(UUID.fromString(value));
    }

    public UUID value() {
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
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
