package com.xavier.servicematchbackend.geomatching.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProvidersMatched(UUID requestId,
                               UUID categoryId,
                               List<UUID> providerIds,
                               Instant matchedAt) {

    public ProvidersMatched {
        if (requestId == null) {
            throw new IllegalArgumentException("requestId must not be null");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId must not be null");
        }
        if (providerIds == null) {
            throw new IllegalArgumentException("providerIds must not be null");
        }
        if (matchedAt == null) {
            throw new IllegalArgumentException("matchedAt must not be null");
        }
    }
}
