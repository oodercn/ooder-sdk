package net.ooder.sdk.llm.model;

import java.util.List;

/**
 * 场景消息 - llm-sdk 2.3
 */
public class SceneMessage {

    private String messageId;
    private String sceneId;
    private String senderId;
    private SenderType senderType;
    private String content;
    private long timestamp;
    private MessageType type;
    private List<String> targetAgents;
    private String replyTo;

    public enum SenderType {
        USER,
        AGENT,
        COLLABORATION
    }

    public enum MessageType {
        TEXT,
        COMMAND,
        EVENT,
        COLLABORATION
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public List<String> getTargetAgents() {
        return targetAgents;
    }

    public void setTargetAgents(List<String> targetAgents) {
        this.targetAgents = targetAgents;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
}
