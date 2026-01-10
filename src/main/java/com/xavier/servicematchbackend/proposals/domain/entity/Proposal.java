package com.xavier.servicematchbackend.proposals.domain.entity;

import com.xavier.servicematchbackend.proposals.domain.valueobject.ProposalStatus;
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
@Table(name = "proposals")
public class Proposal {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "message", length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProposalStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Proposal() {
    }

    private Proposal(UUID id,
                     UUID requestId,
                     UUID providerId,
                     String message,
                     ProposalStatus status,
                     Instant now) {
        this.id = requireNonNull(id, "id must not be null");
        this.requestId = requireNonNull(requestId, "requestId must not be null");
        this.providerId = requireNonNull(providerId, "providerId must not be null");
        this.message = normalizeMessage(message);
        this.status = requireNonNull(status, "status must not be null");
        this.createdAt = requireNonNull(now, "createdAt must not be null");
        this.updatedAt = now;
    }

    public static Proposal create(UUID requestId,
                                  UUID providerId,
                                  String message,
                                  Instant now) {
        return new Proposal(UUID.randomUUID(), requestId, providerId, message, ProposalStatus.SENT, now);
    }

    public UUID id() {
        return id;
    }

    public UUID requestId() {
        return requestId;
    }

    public UUID providerId() {
        return providerId;
    }

    public String message() {
        return message;
    }

    public ProposalStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    private static String normalizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        return message.trim();
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
        Proposal proposal = (Proposal) o;
        return Objects.equals(id, proposal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
