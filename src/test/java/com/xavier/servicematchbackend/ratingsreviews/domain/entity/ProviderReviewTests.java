package com.xavier.servicematchbackend.ratingsreviews.domain.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProviderReviewTests {

    @Test
    void createRejectsRatingOutOfRange() {
        assertThatThrownBy(() -> ProviderReview.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                0,
                "ok",
                Instant.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rating must be between 1 and 5");

        assertThatThrownBy(() -> ProviderReview.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                6,
                "ok",
                Instant.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rating must be between 1 and 5");
    }

    @Test
    void createRejectsBlankComment() {
        assertThatThrownBy(() -> ProviderReview.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                5,
                " ",
                Instant.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("comment must not be blank");
    }

    @Test
    void createRejectsLongComment() {
        String comment = "a".repeat(ProviderReview.MAX_COMMENT_LENGTH + 1);

        assertThatThrownBy(() -> ProviderReview.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                5,
                comment,
                Instant.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("comment must be at most");
    }
}
