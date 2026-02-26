package net.ooder.scene.skills.ai;

import java.util.Map;

/**
 * AIGC 执行结果
 */
public class AIGCResult {

    private boolean success;
    private String resultId;
    private String content;
    private String modelId;
    private long tokensUsed;
    private long executionTime;
    private Map<String, Object> metadata;
    private String errorMessage;

    public static AIGCResult success(String resultId, String content, String modelId) {
        AIGCResult result = new AIGCResult();
        result.success = true;
        result.resultId = resultId;
        result.content = content;
        result.modelId = modelId;
        return result;
    }

    public static AIGCResult failure(String errorMessage) {
        AIGCResult result = new AIGCResult();
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

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }

    public long getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(long tokensUsed) { this.tokensUsed = tokensUsed; }

    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
