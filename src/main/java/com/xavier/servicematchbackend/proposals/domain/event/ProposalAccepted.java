package com.xavier.servicematchbackend.proposals.domain.event;

import java.time.Instant;
import java.util.UUID;

public record ProposalAccepted(UUID proposalId,
                               UUID requestId,
                               UUID providerId,
                               UUID requesterId,
                               Instant acceptedAt) {

    public ProposalAccepted {
        if (proposalId == null) {
            throw new IllegalArgumentException("proposalId must not be null");
        }
        if (requestId == null) {
            throw new IllegalArgumentException("requestId must not be null");
        }
        if (providerId == null) {
            throw new IllegalArgumentException("providerId must not be null");
        }
        if (requesterId == null) {
            throw new IllegalArgumentException("requesterId must not be null");
        }
        if (acceptedAt == null) {
            throw new IllegalArgumentException("acceptedAt must not be null");
        }
    }
}
