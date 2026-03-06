package com.babylon.insurance.shared.exception;

/**
 * Unchecked exception thrown when an encryption or decryption operation fails.
 *
 * <p>The message must never contain plain-text PII or the cipher-text value.
 */
public class EncryptionException extends RuntimeException {

    /**
     * Constructs a new {@code EncryptionException} with a generic message.
     *
     * @param message non-sensitive description of the failure
     * @param cause   the underlying cryptographic exception
     */
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
