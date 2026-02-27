package net.ooder.sdk.llm.model;

/**
 * 协作消息 - 智能体间通信
 */
public class CollaborationMessage {

    private String sessionId;
    private String fromAgentId;
    private String toAgentId;
    private CollaborationMessageType type;
    private String content;
    private long timestamp;

    public enum CollaborationMessageType {
        TASK_ASSIGNMENT,
        TASK_RESULT,
        QUESTION,
        ANSWER,
        SUGGESTION,
        AGREE,
        DISAGREE,
        SUMMARY
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFromAgentId() {
        return fromAgentId;
    }

    public void setFromAgentId(String fromAgentId) {
        this.fromAgentId = fromAgentId;
    }

    public String getToAgentId() {
        return toAgentId;
    }

    public void setToAgentId(String toAgentId) {
        this.toAgentId = toAgentId;
    }

    public CollaborationMessageType getType() {
        return type;
    }

    public void setType(CollaborationMessageType type) {
        this.type = type;
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
}
