package com.xavier.servicematchbackend.proposals.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProposalTests {

    @Test
    void createNormalizesBlankMessage() {
        Proposal proposal = Proposal.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "  ",
                Instant.now()
        );

        assertThat(proposal.message()).isNull();
    }

    @Test
    void updateStatusRejectsNull() {
        Proposal proposal = Proposal.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Posso ajudar",
                Instant.now()
        );

        assertThatThrownBy(() -> proposal.updateStatus(null, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("status must not be null");
    }
}
