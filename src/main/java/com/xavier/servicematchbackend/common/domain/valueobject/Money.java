package com.xavier.servicematch.common.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object representing a monetary amount.
 *
 * <p>
 * Ensures non-negative values and fixed scale (2 decimal places).
 * Immutable and safe for financial calculations.
 * </p>
 */
public final class Money {

    private static final int SCALE = 2;

    private final BigDecimal amount;

    /**
     * Creates a Money instance.
     *
     * @param amount non-null and non-negative amount
     * @throws IllegalArgumentException if amount is null or negative
     */
    public Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }
        this.amount = amount.setScale(SCALE, RoundingMode.HALF_EVEN);
    }

    /**
     * @return monetary amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Adds another Money value.
     *
     * @param other money to add
     * @return new Money instance
     */
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    /**
     * Subtracts another Money value.
     *
     * @param other money to subtract
     * @return new Money instance
     */
    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}