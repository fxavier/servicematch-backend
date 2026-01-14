package com.xavier.servicematchbackend.notifications.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.xavier.servicematchbackend.notifications.domain.valueobject.NotificationStatus;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NotificationLogTests {

    @Test
    void createRejectsBlankTitle() {
        assertThatThrownBy(() -> NotificationLog.create(
                UUID.randomUUID(),
                "EVENT",
                " ",
                "body",
                null,
                Instant.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title must not be blank");
    }

    @Test
    void markFailedRejectsBlankReason() {
        NotificationLog log = NotificationLog.create(
                UUID.randomUUID(),
                "EVENT",
                "title",
                "body",
                null,
                Instant.now()
        );

        assertThatThrownBy(() -> log.markFailed(" ", Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("failureReason must not be blank");
    }

    @Test
    void markSentSetsStatus() {
        NotificationLog log = NotificationLog.create(
                UUID.randomUUID(),
                "EVENT",
                "title",
                "body",
                null,
                Instant.now()
        );

        log.markSent(Instant.now());

        assertThat(log.status()).isEqualTo(NotificationStatus.SENT);
        assertThat(log.dispatchedAt()).isNotNull();
    }
}
