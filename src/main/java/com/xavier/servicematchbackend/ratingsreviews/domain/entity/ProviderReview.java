package com.xavier.servicematchbackend.ratingsreviews.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "provider_reviews")
public class ProviderReview {

    public static final int MAX_COMMENT_LENGTH = 1000;

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "comment", nullable = false, length = MAX_COMMENT_LENGTH)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ProviderReview() {
    }

    private ProviderReview(UUID id,
                           UUID requestId,
                           UUID providerId,
                           UUID requesterId,
                           int rating,
                           String comment,
                           Instant createdAt) {
        this.id = requireNonNull(id, "id must not be null");
        this.requestId = requireNonNull(requestId, "requestId must not be null");
        this.providerId = requireNonNull(providerId, "providerId must not be null");
        this.requesterId = requireNonNull(requesterId, "requesterId must not be null");
        this.rating = normalizeRating(rating);
        this.comment = normalizeComment(comment);
        this.createdAt = requireNonNull(createdAt, "createdAt must not be null");
    }

    public static ProviderReview create(UUID requestId,
                                        UUID providerId,
                                        UUID requesterId,
                                        int rating,
                                        String comment,
                                        Instant createdAt) {
        return new ProviderReview(
                UUID.randomUUID(),
                requestId,
                providerId,
                requesterId,
                rating,
                comment,
                createdAt
        );
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

    public UUID requesterId() {
        return requesterId;
    }

    public int rating() {
        return rating;
    }

    public String comment() {
        return comment;
    }

    public Instant createdAt() {
        return createdAt;
    }

    private static int normalizeRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }
        return rating;
    }

    private static String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("comment must not be blank");
        }
        String trimmed = comment.trim();
        if (trimmed.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException("comment must be at most " + MAX_COMMENT_LENGTH + " characters");
        }
        return trimmed;
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
        ProviderReview that = (ProviderReview) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
