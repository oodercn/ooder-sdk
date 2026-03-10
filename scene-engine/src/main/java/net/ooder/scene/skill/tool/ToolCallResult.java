package net.ooder.scene.skill.tool;

/**
 * 工具调用结果
 *
 * @author ooder
 * @since 2.3
 */
public class ToolCallResult {
    
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILURE = "failure";
    public static final String STATUS_TIMEOUT = "timeout";
    public static final String STATUS_SKIPPED = "skipped";
    
    private String toolCallId;
    private String toolName;
    private ToolResult toolResult;
    private long executionTime;
    private String status;
    
    public ToolCallResult() {
    }
    
    public ToolCallResult(String toolCallId, String toolName, ToolResult toolResult) {
        this.toolCallId = toolCallId;
        this.toolName = toolName;
        this.toolResult = toolResult;
        this.status = toolResult.isSuccess() ? STATUS_SUCCESS : STATUS_FAILURE;
    }
    
    public static ToolCallResult success(String toolCallId, String toolName, ToolResult result) {
        ToolCallResult tcr = new ToolCallResult(toolCallId, toolName, result);
        tcr.setStatus(STATUS_SUCCESS);
        return tcr;
    }
    
    public static ToolCallResult failure(String toolCallId, String toolName, String errorMessage) {
        ToolCallResult tcr = new ToolCallResult();
        tcr.setToolCallId(toolCallId);
        tcr.setToolName(toolName);
        tcr.setToolResult(ToolResult.failure(STATUS_FAILURE, errorMessage));
        tcr.setStatus(STATUS_FAILURE);
        return tcr;
    }

    public static ToolCallResult timeout(String toolCallId, String toolName) {
        ToolCallResult tcr = new ToolCallResult();
        tcr.setToolCallId(toolCallId);
        tcr.setToolName(toolName);
        tcr.setToolResult(ToolResult.failure(STATUS_TIMEOUT, "Tool execution timeout"));
        tcr.setStatus(STATUS_TIMEOUT);
        return tcr;
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
    
    public ToolResult getToolResult() {
        return toolResult;
    }
    
    public void setToolResult(ToolResult toolResult) {
        this.toolResult = toolResult;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(status);
    }
}
