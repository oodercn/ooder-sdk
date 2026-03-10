package net.ooder.scene.skill.tool;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具执行上下文
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class ToolExecutionContext extends ToolContext {

    private String executionId;
    private long startTime;
    private Map<String, Object> executionMetadata = new HashMap<>();

    public ToolExecutionContext() {
        super();
        this.startTime = System.currentTimeMillis();
    }

    public ToolExecutionContext(String sessionId, String userId) {
        super(sessionId, userId);
        this.startTime = System.currentTimeMillis();
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Map<String, Object> getExecutionMetadata() {
        return executionMetadata;
    }

    public void setExecutionMetadata(Map<String, Object> executionMetadata) {
        this.executionMetadata = executionMetadata;
    }

    public void setConversationId(String conversationId) {
        setAttribute("conversationId", conversationId);
    }

    public String getConversationId() {
        return getAttribute("conversationId");
    }

    public static ToolExecutionContext of(String sessionId, String userId) {
        return new ToolExecutionContext(sessionId, userId);
    }
}
