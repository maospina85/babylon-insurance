package com.babylon.insurance.shared.encryption;

/**
 * Port for symmetric encryption of sensitive personal data.
 *
 * <p>Implementations must use AES-256-GCM with a random IV per operation.
 * Neither plain-text inputs nor cipher-text outputs should be logged.
 */
public interface EncryptionPort {

    /**
     * Encrypts a plain-text string.
     *
     * @param plaintext the value to encrypt; must not be {@code null}
     * @return Base64-encoded string containing the IV and ciphertext
     * @throws com.babylon.insurance.shared.exception.EncryptionException if encryption fails
     */
    String encrypt(String plaintext);

    /**
     * Decrypts a previously encrypted value.
     *
     * @param ciphertext Base64-encoded IV + ciphertext produced by {@link #encrypt(String)}
     * @return the recovered plain-text string
     * @throws com.babylon.insurance.shared.exception.EncryptionException if decryption fails
     */
    String decrypt(String ciphertext);
}
