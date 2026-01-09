package com.xavier.servicematch.common.domain.exception;


/**
 * Base class for domain-specific exceptions.
 *
 * <p>
 * Domain exceptions represent errors that occur within the business logic
 * of the application. They are used to signal violations of business rules
 * or other domain-related issues.
 * </p>
 */
public class DomainException extends  RuntimeException {

    /**
     * Creates a new domain exception with the specified message and code.
     * Stable error code to support API mapping and client handling.
     *
     * @param message detailed error message
     * @param code    specific error code
     */
    private final String code;

    /**
     * Creates a new DomainException with the specified message and code.
     * @param message
     * @param code
     */
    public DomainException(String message, String code) {
        super(message);
        this.code = code;
    }

    /**
     * Creates a new DomainException with the specified message, code and cause.
     * @param message
     * @param code
     * @param cause
     */
    public DomainException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
