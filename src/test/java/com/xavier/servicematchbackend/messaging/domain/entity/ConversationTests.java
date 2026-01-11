package com.xavier.servicematchbackend.messaging.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConversationTests {

    @Test
    void isParticipantReturnsTrueForParticipants() {
        UUID requestId = UUID.randomUUID();
        UUID proposalId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        Conversation conversation = Conversation.create(
                requestId,
                proposalId,
                requesterId,
                providerId,
                Instant.now()
        );

        assertThat(conversation.isParticipant(requesterId)).isTrue();
        assertThat(conversation.isParticipant(providerId)).isTrue();
        assertThat(conversation.isParticipant(UUID.randomUUID())).isFalse();
    }
}
