package com.xavier.servicematchbackend.messaging.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "proposal_id", nullable = false)
    private UUID proposalId;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Conversation() {
    }

    private Conversation(UUID requestId,
                         UUID proposalId,
                         UUID requesterId,
                         UUID providerId,
                         Instant now) {
        this.requestId = requireNonNull(requestId, "requestId must not be null");
        this.proposalId = requireNonNull(proposalId, "proposalId must not be null");
        this.requesterId = requireNonNull(requesterId, "requesterId must not be null");
        this.providerId = requireNonNull(providerId, "providerId must not be null");
        this.createdAt = requireNonNull(now, "createdAt must not be null");
        this.updatedAt = now;
    }

    public static Conversation create(UUID requestId,
                                      UUID proposalId,
                                      UUID requesterId,
                                      UUID providerId,
                                      Instant now) {
        return new Conversation(requestId, proposalId, requesterId, providerId, now);
    }

    public UUID id() {
        return requestId;
    }

    public UUID requestId() {
        return requestId;
    }

    public UUID proposalId() {
        return proposalId;
    }

    public UUID requesterId() {
        return requesterId;
    }

    public UUID providerId() {
        return providerId;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public void touch(Instant now) {
        this.updatedAt = requireNonNull(now, "updatedAt must not be null");
    }

    public boolean isParticipant(UUID userId) {
        return Objects.equals(requesterId, userId) || Objects.equals(providerId, userId);
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
        Conversation that = (Conversation) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
}
