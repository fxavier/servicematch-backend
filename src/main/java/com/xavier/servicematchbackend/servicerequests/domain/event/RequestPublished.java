package com.xavier.servicematchbackend.servicerequests.domain.event;

import java.time.Instant;
import java.util.UUID;

public record RequestPublished(UUID requestId,
                               UUID requesterId,
                               UUID categoryId,
                               String description,
                               double locationLat,
                               double locationLng,
                               Instant publishedAt) {

    public RequestPublished {
        if (requestId == null) {
            throw new IllegalArgumentException("requestId must not be null");
        }
        if (requesterId == null) {
            throw new IllegalArgumentException("requesterId must not be null");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId must not be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (publishedAt == null) {
            throw new IllegalArgumentException("publishedAt must not be null");
        }
    }
}
