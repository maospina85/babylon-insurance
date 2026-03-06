package com.babylon.insurance.shared.exception;

/**
 * Thrown when a requested resource does not exist in the system.
 *
 * <p>Results in an HTTP {@code 404 Not Found} response.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceNotFoundException}.
     *
     * @param message a description of which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
