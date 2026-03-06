package com.babylon.insurance.shared.exception;

/**
 * Thrown when a quote request violates a business rule (domain validation).
 *
 * <p>Results in an HTTP {@code 422 Unprocessable Entity} response.
 */
public class QuoteValidationException extends RuntimeException {

    /**
     * Constructs a new {@code QuoteValidationException}.
     *
     * @param message a business-readable description of the violation
     */
    public QuoteValidationException(String message) {
        super(message);
    }
}
