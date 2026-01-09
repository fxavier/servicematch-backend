package com.xavier.servicematch.common.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing an email address.
 *
 * <p>
 * Ensures normalization (lowercase, trimmed) and
 * enforces valid email format at creation time.
 * </p>
 *
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private final String value;

    /**
     * Creates a new Email value object.
     *
     * @param value raw email string
     * @throws IllegalArgumentException if null, empty or invalid format
     */
    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String normalized = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        this.value = normalized;
    }

    /**
     * @return normalized email value
     */
    public String value() {
        return value;
    }

    /**
     * @return email domain (part after '@')
     */
    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}