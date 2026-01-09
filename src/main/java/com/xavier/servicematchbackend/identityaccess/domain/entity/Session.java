package com.xavier.servicematchbackend.identityaccess.domain.entity;

import com.xavier.servicematchbackend.identityaccess.domain.valueobject.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "sessions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_sessions_refresh_token_hash",
                columnNames = "refresh_token_hash")
)
public class Session {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "user_id", nullable = false, updatable = false))
    private UserId userId;

    @Column(name = "refresh_token_hash", nullable = false, length = 64)
    private String refreshTokenHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by")
    private UUID replacedBy;

    protected Session() {
    }

    private Session(UUID id,
                    UserId userId,
                    String refreshTokenHash,
                    Instant createdAt,
                    Instant expiresAt) {
        this.id = requireNonNull(id, "id must not be null");
        this.userId = requireNonNull(userId, "userId must not be null");
        this.refreshTokenHash = requireNonBlank(refreshTokenHash, "refreshTokenHash must not be blank");
        this.createdAt = requireNonNull(createdAt, "createdAt must not be null");
        this.expiresAt = requireNonNull(expiresAt, "expiresAt must not be null");
        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("expiresAt must be after createdAt");
        }
    }

    public static Session create(UserId userId, String refreshTokenHash, Instant createdAt, Instant expiresAt) {
        return new Session(UUID.randomUUID(), userId, refreshTokenHash, createdAt, expiresAt);
    }

    public UUID id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public String refreshTokenHash() {
        return refreshTokenHash;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public Instant revokedAt() {
        return revokedAt;
    }

    public UUID replacedBy() {
        return replacedBy;
    }

    public boolean isActive(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void revoke(Instant now, UUID replacedBy) {
        if (revokedAt != null) {
            return;
        }
        revokedAt = requireNonNull(now, "revokedAt must not be null");
        this.replacedBy = replacedBy;
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
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
        Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
