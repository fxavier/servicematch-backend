package com.xavier.servicematchbackend.identityaccess.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class PasswordHash {

    @Column(name = "password_hash", nullable = false, length = 255)
    private String value;

    protected PasswordHash() {
    }

    private PasswordHash(String value) {
        this.value = value;
    }

    public static PasswordHash of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("passwordHash must not be blank");
        }
        return new PasswordHash(value);
    }

    public String value() {
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
        PasswordHash that = (PasswordHash) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "****";
    }
}
