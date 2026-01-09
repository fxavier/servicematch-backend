package com.xavier.servicematchbackend.common.domain.identifier;

import lombok.Getter;

import java.util.Objects;

/**
 * Base class for strongly-typed identifiers.
 * This abstration prevents accidental misuse of identifiers
 * from different aggregates (e.g, using a UserId where an OrderId is expected).
 *
 * @param <T> underlying type of the identifier (e.g., Long, UUID, String).
 */

@Getter
public abstract class BaseId<T> {

    /**
     * Underlying identifier value.
     * Immutablr by design.
     *
     */

    protected final T value;

    /**
     * Creates a new Identifier.
     *
     * @param value non-null identifier value
     * @throws IllegalArgumentException if value is null
     */

    protected BaseId(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Identifier value cannot be null");
        }
        this.value = value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseId<?> baseId = (BaseId<?>) o;

        return value.equals(baseId.value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(value);
    }
}
