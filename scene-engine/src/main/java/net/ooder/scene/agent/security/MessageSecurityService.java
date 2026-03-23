package net.ooder.scene.agent.security;

import net.ooder.scene.agent.AgentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MessageSecurityService {

    private static final Logger log = LoggerFactory.getLogger(MessageSecurityService.class);

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final long REPLAY_WINDOW_MS = 5 * 60 * 1000L;

    private String secretKey;
    private boolean signatureEnabled;
    private boolean replayProtectionEnabled;

    public MessageSecurityService() {
        this.secretKey = "ooder-default-message-secret";
        this.signatureEnabled = true;
        this.replayProtectionEnabled = true;
    }

    public MessageSecurityService(String secretKey) {
        this();
        this.secretKey = secretKey;
    }

    public SecureMessage sign(AgentMessage message) {
        if (message == null) {
            return null;
        }

        SecureMessage secureMessage = new SecureMessage(message);

        if (signatureEnabled) {
            String payload = buildSignaturePayload(message);
            String signature = computeHmac(payload);
            secureMessage.setSignature(signature);
        }

        secureMessage.setTimestamp(System.currentTimeMillis());
        secureMessage.setSignatureEnabled(signatureEnabled);

        return secureMessage;
    }

    public boolean verify(SecureMessage secureMessage) {
        if (secureMessage == null || secureMessage.getMessage() == null) {
            return false;
        }

        if (!signatureEnabled) {
            return true;
        }

        if (!secureMessage.isSignatureEnabled()) {
            log.warn("Message signature not enabled: messageId={}", secureMessage.getMessage().getMessageId());
            return true;
        }

        if (replayProtectionEnabled && !verifyTimestamp(secureMessage.getTimestamp())) {
            log.warn("Message replay detected: messageId={}, timestamp={}",
                    secureMessage.getMessage().getMessageId(), secureMessage.getTimestamp());
            return false;
        }

        String payload = buildSignaturePayload(secureMessage.getMessage());
        String expectedSignature = computeHmac(payload);

        boolean valid = java.security.MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                secureMessage.getSignature().getBytes(StandardCharsets.UTF_8)
        );

        if (!valid) {
            log.warn("Message signature verification failed: messageId={}",
                    secureMessage.getMessage().getMessageId());
        }

        return valid;
    }

    public boolean verify(AgentMessage message, String signature, long timestamp) {
        SecureMessage secureMessage = new SecureMessage(message);
        secureMessage.setSignature(signature);
        secureMessage.setTimestamp(timestamp);
        secureMessage.setSignatureEnabled(true);
        return verify(secureMessage);
    }

    private String buildSignaturePayload(AgentMessage message) {
        StringBuilder sb = new StringBuilder();
        sb.append(message.getMessageId());
        sb.append("|");
        sb.append(message.getFromAgent() != null ? message.getFromAgent() : "");
        sb.append("|");
        sb.append(message.getToAgent() != null ? message.getToAgent() : "");
        sb.append("|");
        sb.append(message.getType() != null ? message.getType().getCode() : "");
        sb.append("|");
        sb.append(message.getCreateTime());
        sb.append("|");
        sb.append(message.getPayload() != null ? message.getPayload().hashCode() : 0);
        return sb.toString();
    }

    private String computeHmac(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(keySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to compute HMAC", e);
            throw new RuntimeException("Failed to compute HMAC", e);
        }
    }

    private boolean verifyTimestamp(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = Math.abs(now - timestamp);
        return diff <= REPLAY_WINDOW_MS;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setSignatureEnabled(boolean signatureEnabled) {
        this.signatureEnabled = signatureEnabled;
    }

    public void setReplayProtectionEnabled(boolean replayProtectionEnabled) {
        this.replayProtectionEnabled = replayProtectionEnabled;
    }

    public boolean isSignatureEnabled() {
        return signatureEnabled;
    }
}
