package com.xavier.servicematchbackend.messaging.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConversationMessageTests {

    @Test
    void createRejectsBlankBody() {
        assertThatThrownBy(() -> ConversationMessage.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                " ",
                Instant.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("body must not be blank");
    }

    @Test
    void createRejectsLongBody() {
        String body = "a".repeat(ConversationMessage.MAX_BODY_LENGTH + 1);

        assertThatThrownBy(() -> ConversationMessage.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                body,
                Instant.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("body must be at most");
    }

    @Test
    void createTrimsBody() {
        ConversationMessage message = ConversationMessage.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                " ola ",
                Instant.now()
        );

        assertThat(message.body()).isEqualTo("ola");
    }
}
