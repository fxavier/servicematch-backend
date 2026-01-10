package com.xavier.servicematchbackend.proposals.application.usecase;

import com.xavier.servicematchbackend.proposals.application.dto.ProposalCreateRequest;
import com.xavier.servicematchbackend.proposals.application.dto.ProposalResponse;
import com.xavier.servicematchbackend.proposals.domain.entity.Proposal;
import com.xavier.servicematchbackend.proposals.domain.event.ProposalAccepted;
import com.xavier.servicematchbackend.proposals.domain.event.ProposalSubmitted;
import com.xavier.servicematchbackend.proposals.domain.valueobject.ProposalStatus;
import com.xavier.servicematchbackend.proposals.infra.persistence.ProposalRepository;
import com.xavier.servicematchbackend.servicerequests.application.usecase.ServiceRequestService;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final ServiceRequestService serviceRequestService;
    private final ApplicationEventPublisher eventPublisher;

    public ProposalService(ProposalRepository proposalRepository,
                           ServiceRequestService serviceRequestService,
                           ApplicationEventPublisher eventPublisher) {
        this.proposalRepository = proposalRepository;
        this.serviceRequestService = serviceRequestService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ProposalResponse submit(UUID providerId, ProposalCreateRequest request) {
        ProposalCreateRequest input = requireRequest(request);
        UUID requestId = parseUuid(input.requestId(), "requestId");
        UUID providerUuid = requireProvider(providerId);

        serviceRequestService.assertRequestAcceptsProposals(requestId);
        if (proposalRepository.existsByRequestIdAndProviderId(requestId, providerUuid)) {
            throw new IllegalArgumentException("proposal already submitted");
        }

        Instant now = Instant.now();
        Proposal proposal = Proposal.create(requestId, providerUuid, input.message(), now);
        proposalRepository.save(proposal);
        eventPublisher.publishEvent(new ProposalSubmitted(
                proposal.id(),
                proposal.requestId(),
                proposal.providerId(),
                now
        ));
        return ProposalResponse.from(proposal);
    }

    @Transactional
    public ProposalResponse accept(UUID requesterId, UUID proposalId) {
        UUID requesterUuid = requireRequester(requesterId);
        UUID proposalUuid = requireProposal(proposalId);
        Proposal proposal = proposalRepository.findById(proposalUuid)
                .orElseThrow(() -> new IllegalArgumentException("proposal not found"));

        if (proposal.status() == ProposalStatus.ACCEPTED) {
            return ProposalResponse.from(proposal);
        }
        if (proposal.status() != ProposalStatus.SENT) {
            throw new IllegalArgumentException("proposal cannot be accepted");
        }

        serviceRequestService.bookRequest(proposal.requestId(), requesterUuid);

        Instant now = Instant.now();
        proposal.updateStatus(ProposalStatus.ACCEPTED, now);
        proposalRepository.save(proposal);
        proposalRepository.rejectOtherProposals(
                proposal.requestId(),
                proposal.id(),
                ProposalStatus.SENT,
                ProposalStatus.REJECTED,
                now
        );

        eventPublisher.publishEvent(new ProposalAccepted(
                proposal.id(),
                proposal.requestId(),
                proposal.providerId(),
                requesterUuid,
                now
        ));
        return ProposalResponse.from(proposal);
    }

    private ProposalCreateRequest requireRequest(ProposalCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        return request;
    }

    private UUID requireProvider(UUID providerId) {
        if (providerId == null) {
            throw new IllegalArgumentException("providerId must not be null");
        }
        return providerId;
    }

    private UUID requireRequester(UUID requesterId) {
        if (requesterId == null) {
            throw new IllegalArgumentException("requesterId must not be null");
        }
        return requesterId;
    }

    private UUID requireProposal(UUID proposalId) {
        if (proposalId == null) {
            throw new IllegalArgumentException("proposalId must not be null");
        }
        return proposalId;
    }

    private UUID parseUuid(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(field + " must be a valid UUID");
        }
    }
}
