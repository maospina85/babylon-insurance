package com.babylon.insurance.shared.encryption;

import com.babylon.insurance.shared.exception.EncryptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AES-GCM Encryption Adapter")
class AesGcmEncryptionTest {

    private AesGcmEncryptionAdapter encryption;

    /** 32-byte AES-256 key encoded in Base64 for testing. */
    private static final String TEST_KEY =
            Base64.getEncoder().encodeToString(new byte[32]);

    @BeforeEach
    void setUp() {
        encryption = new AesGcmEncryptionAdapter(TEST_KEY);
    }

    @Test
    @DisplayName("dado un texto plano cuando cifra y descifra entonces recupera el original")
    void givenPlaintext_whenEncryptThenDecrypt_thenRecoverOriginal() {
        String original = "María García López";

        String ciphertext = encryption.encrypt(original);
        String recovered  = encryption.decrypt(ciphertext);

        assertThat(recovered).isEqualTo(original);
    }

    @Test
    @DisplayName("dado el mismo texto cuando cifra dos veces entonces produce ciphertexts distintos (IV aleatorio)")
    void givenSamePlaintext_whenEncryptTwice_thenProduceDifferentCiphertexts() {
        String plaintext = "test@email.com";

        String first  = encryption.encrypt(plaintext);
        String second = encryption.encrypt(plaintext);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("dado ciphertext corrupto cuando descifra entonces lanza EncryptionException")
    void givenCorruptedCiphertext_whenDecrypt_thenThrowEncryptionException() {
        String corrupted = Base64.getEncoder().encodeToString("not-a-valid-gcm-payload".getBytes());

        assertThatThrownBy(() -> encryption.decrypt(corrupted))
                .isInstanceOf(EncryptionException.class);
    }

    @Test
    @DisplayName("dado texto vacío cuando cifra entonces cifra correctamente y se puede descifrar")
    void givenEmptyString_whenEncrypt_thenEncryptsAndDecryptsCorrectly() {
        String empty = "";

        String ciphertext = encryption.encrypt(empty);
        String recovered  = encryption.decrypt(ciphertext);

        assertThat(recovered).isEmpty();
    }

    @Test
    @DisplayName("dado clave de longitud incorrecta cuando crea adaptador entonces lanza IllegalArgumentException")
    void givenWrongKeyLength_whenConstruct_thenThrowIllegalArgumentException() {
        String shortKey = Base64.getEncoder().encodeToString(new byte[16]); // 16 bytes, not 32

        assertThatThrownBy(() -> new AesGcmEncryptionAdapter(shortKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("32 bytes");
    }

    @Test
    @DisplayName("dado clave vacía cuando crea adaptador entonces lanza IllegalArgumentException")
    void givenBlankKey_whenConstruct_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new AesGcmEncryptionAdapter(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
