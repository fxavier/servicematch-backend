package com.xavier.servicematchbackend.proposals.infra.persistence;

import com.xavier.servicematchbackend.proposals.domain.entity.Proposal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

    boolean existsByRequestIdAndProviderId(UUID requestId, UUID providerId);
}
