package net.ooder.scene.skills.ai;

import java.util.Map;

/**
 * MCP 调用结果
 */
public class MCPResult {

    private boolean success;
    private String resultId;
    private String content;
    private Map<String, Object> data;
    private String errorMessage;
    private long executionTime;

    public static MCPResult success(String resultId, String content) {
        MCPResult result = new MCPResult();
        result.success = true;
        result.resultId = resultId;
        result.content = content;
        return result;
    }

    public static MCPResult success(String resultId, Map<String, Object> data) {
        MCPResult result = new MCPResult();
        result.success = true;
        result.resultId = resultId;
        result.data = data;
        return result;
    }

    public static MCPResult failure(String errorMessage) {
        MCPResult result = new MCPResult();
        result.success = false;
        result.errorMessage = errorMessage;
        return result;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
}
