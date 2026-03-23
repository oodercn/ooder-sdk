package net.ooder.scene.agent.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialHasherTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGenerateSalt() {
        String salt1 = CredentialHasher.generateSalt();
        String salt2 = CredentialHasher.generateSalt();

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2);
    }

    @Test
    void testHash() {
        String plainValue = "my-secret-password";
        String salt = CredentialHasher.generateSalt();

        String hash1 = CredentialHasher.hash(plainValue, salt);
        String hash2 = CredentialHasher.hash(plainValue, salt);

        assertNotNull(hash1);
        assertEquals(hash1, hash2);
    }

    @Test
    void testVerify() {
        String plainValue = "my-secret-password";
        String salt = CredentialHasher.generateSalt();
        String hash = CredentialHasher.hash(plainValue, salt);

        assertTrue(CredentialHasher.verify(plainValue, hash, salt));
        assertFalse(CredentialHasher.verify("wrong-password", hash, salt));
    }

    @Test
    void testGenerateSecureToken() {
        String token1 = CredentialHasher.generateSecureToken(32);
        String token2 = CredentialHasher.generateSecureToken(32);

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    void testGenerateApiKey() {
        String apiKey = CredentialHasher.generateApiKey();

        assertNotNull(apiKey);
        assertTrue(apiKey.startsWith("sk-"));
    }
}
