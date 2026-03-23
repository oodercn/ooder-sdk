package net.ooder.scene.agent.security;

import net.ooder.scene.agent.AgentMessage;

public class SecureMessage {

    private AgentMessage message;
    private String signature;
    private long timestamp;
    private boolean signatureEnabled;
    private String encryptionType;

    public SecureMessage() {
        this.signatureEnabled = false;
        this.encryptionType = "none";
    }

    public SecureMessage(AgentMessage message) {
        this();
        this.message = message;
    }

    public AgentMessage getMessage() {
        return message;
    }

    public void setMessage(AgentMessage message) {
        this.message = message;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSignatureEnabled() {
        return signatureEnabled;
    }

    public void setSignatureEnabled(boolean signatureEnabled) {
        this.signatureEnabled = signatureEnabled;
    }

    public String getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(String encryptionType) {
        this.encryptionType = encryptionType;
    }

    public String getMessageId() {
        return message != null ? message.getMessageId() : null;
    }

    public String getFromAgent() {
        return message != null ? message.getFromAgent() : null;
    }

    public String getToAgent() {
        return message != null ? message.getToAgent() : null;
    }

    @Override
    public String toString() {
        return "SecureMessage{" +
                "messageId='" + getMessageId() + '\'' +
                ", fromAgent='" + getFromAgent() + '\'' +
                ", toAgent='" + getToAgent() + '\'' +
                ", signatureEnabled=" + signatureEnabled +
                '}';
    }
}
