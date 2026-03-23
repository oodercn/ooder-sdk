package net.ooder.scene.agent.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class CredentialHasher {

    private static final Logger log = LoggerFactory.getLogger(CredentialHasher.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;

    private CredentialHasher() {
    }

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String plainValue, String salt) {
        return hash(plainValue, salt, "SHA-256");
    }

    public static String hash(String plainValue, String salt, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            md.update(saltBytes);

            byte[] inputBytes = plainValue.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = md.digest(inputBytes);

            for (int i = 0; i < ITERATIONS; i++) {
                md.reset();
                hashBytes = md.digest(hashBytes);
            }

            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("Hash algorithm not found: {}", algorithm, e);
            throw new RuntimeException("Hash algorithm not found: " + algorithm, e);
        }
    }

    public static boolean verify(String plainValue, String hashedValue, String salt) {
        return verify(plainValue, hashedValue, salt, "SHA-256");
    }

    public static boolean verify(String plainValue, String hashedValue, String salt, String algorithm) {
        if (plainValue == null || hashedValue == null || salt == null) {
            return false;
        }

        String computed = hash(plainValue, salt, algorithm);
        return MessageDigest.isEqual(
                hashedValue.getBytes(StandardCharsets.UTF_8),
                computed.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static String generateSecureToken(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String generateApiKey() {
        return "sk-" + generateSecureToken(32);
    }
}
