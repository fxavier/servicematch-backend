package com.xavier.servicematchbackend.proposals.application.dto;

import com.xavier.servicematchbackend.proposals.domain.entity.Proposal;
import com.xavier.servicematchbackend.proposals.domain.valueobject.ProposalStatus;
import java.time.Instant;

public record ProposalResponse(String id,
                               String requestId,
                               String providerId,
                               String message,
                               ProposalStatus status,
                               Instant createdAt) {

    public static ProposalResponse from(Proposal proposal) {
        return new ProposalResponse(
                proposal.id().toString(),
                proposal.requestId().toString(),
                proposal.providerId().toString(),
                proposal.message(),
                proposal.status(),
                proposal.createdAt()
        );
    }
}
