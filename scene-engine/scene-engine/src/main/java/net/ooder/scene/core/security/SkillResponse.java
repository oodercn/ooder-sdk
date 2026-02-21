package net.ooder.scene.core.security;

import java.util.Map;

/**
 * 技能响应
 */
public class SkillResponse {
    private String requestId;
    private Object result;
    private String error;
    private boolean success;
    private Map<String, Object> metadata;

    public SkillResponse() {}

    public static SkillResponse success(Object result) {
        SkillResponse response = new SkillResponse();
        response.setSuccess(true);
        response.setResult(result);
        return response;
    }

    public static SkillResponse denied(String reason) {
        SkillResponse response = new SkillResponse();
        response.setSuccess(false);
        response.setError(reason);
        return response;
    }

    public static SkillResponse error(String message) {
        SkillResponse response = new SkillResponse();
        response.setSuccess(false);
        response.setError(message);
        return response;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
