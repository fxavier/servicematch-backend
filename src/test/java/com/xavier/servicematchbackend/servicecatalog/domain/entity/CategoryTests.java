package com.xavier.servicematchbackend.servicecatalog.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class CategoryTests {

    @Test
    void createRejectsBlankName() {
        Instant now = Instant.now();

        assertThatThrownBy(() -> Category.create(" ", null, null, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name must not be blank");
    }

    @Test
    void createNormalizesNameAndPath() {
        Instant now = Instant.now();

        Category category = Category.create(" Limpeza ", null, " /limpeza ", now);

        assertThat(category.name()).isEqualTo("Limpeza");
        assertThat(category.path()).isEqualTo("/limpeza");
    }

    @Test
    void updateRejectsBlankName() {
        Instant now = Instant.now();
        Category category = Category.create("Limpeza", null, "/limpeza", now);

        assertThatThrownBy(() -> category.update(" ", null, null, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name must not be blank");
    }
}
