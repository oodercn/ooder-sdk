package net.ooder.scene.skill.conversation;

import net.ooder.scene.skill.tool.ToolCallResult;

import java.util.Date;
import java.util.Map;

/**
 * 工具调用日志
 *
 * <p>记录 Function Calling 的完整执行过程</p>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * @author ooder
 * @since 2.3
 */
public class FunctionCallLog {

    private String logId;
    private String sessionId;
    private String messageId;
    private String toolCallId;
    private String toolName;
    private Map<String, Object> arguments;
    private ToolCallResult result;
    private long executionTime;
    private Date createdAt;

    public FunctionCallLog() {
        this.createdAt = new Date();
    }

    public FunctionCallLog(String logId, String sessionId, String toolCallId, String toolName) {
        this();
        this.logId = logId;
        this.sessionId = sessionId;
        this.toolCallId = toolCallId;
        this.toolName = toolName;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getToolCallId() {
        return toolCallId;
    }

    public void setToolCallId(String toolCallId) {
        this.toolCallId = toolCallId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public ToolCallResult getResult() {
        return result;
    }

    public void setResult(ToolCallResult result) {
        this.result = result;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 检查工具调用是否成功
     */
    public boolean isSuccess() {
        return result != null && result.isSuccess();
    }

    /**
     * 获取工具调用结果内容
     */
    public String getResultContent() {
        if (result != null && result.getToolResult() != null) {
            return result.getToolResult().asText();
        }
        return null;
    }

    @Override
    public String toString() {
        return "FunctionCallLog{" +
                "logId='" + logId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", toolName='" + toolName + '\'' +
                ", executionTime=" + executionTime +
                ", success=" + isSuccess() +
                '}';
    }
}
