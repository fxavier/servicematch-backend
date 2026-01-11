package com.xavier.servicematchbackend.proposals.infra.persistence;

import com.xavier.servicematchbackend.proposals.domain.entity.Proposal;
import com.xavier.servicematchbackend.proposals.domain.valueobject.ProposalStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

    boolean existsByRequestIdAndProviderId(UUID requestId, UUID providerId);

    Optional<Proposal> findByRequestIdAndStatus(UUID requestId, ProposalStatus status);

    @Modifying
    @Query("""
            update Proposal p
               set p.status = :status,
                   p.updatedAt = :updatedAt
             where p.requestId = :requestId
               and p.id <> :acceptedId
               and p.status = :sentStatus
            """)
    int rejectOtherProposals(@Param("requestId") UUID requestId,
                             @Param("acceptedId") UUID acceptedId,
                             @Param("sentStatus") ProposalStatus sentStatus,
                             @Param("status") ProposalStatus status,
                             @Param("updatedAt") java.time.Instant updatedAt);
}
