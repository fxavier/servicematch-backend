package com.xavier.servicematchbackend.proposals.domain.event;

import java.time.Instant;
import java.util.UUID;

public record ProposalSubmitted(UUID proposalId,
                                UUID requestId,
                                UUID providerId,
                                Instant submittedAt) {

    public ProposalSubmitted {
        if (proposalId == null) {
            throw new IllegalArgumentException("proposalId must not be null");
        }
        if (requestId == null) {
            throw new IllegalArgumentException("requestId must not be null");
        }
        if (providerId == null) {
            throw new IllegalArgumentException("providerId must not be null");
        }
        if (submittedAt == null) {
            throw new IllegalArgumentException("submittedAt must not be null");
        }
    }
}
