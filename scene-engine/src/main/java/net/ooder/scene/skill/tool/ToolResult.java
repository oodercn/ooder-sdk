package net.ooder.scene.skill.tool;

/**
 * 工具执行结果
 *
 * @author ooder
 * @since 2.3
 */
public class ToolResult {
    
    private boolean success;
    private Object data;
    private String errorMessage;
    private String errorCode;
    private long executionTime;
    
    public ToolResult() {
    }
    
    public static ToolResult success(Object data) {
        ToolResult result = new ToolResult();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }
    
    public static ToolResult failure(String errorMessage) {
        ToolResult result = new ToolResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public static ToolResult failure(String errorCode, String errorMessage) {
        ToolResult result = new ToolResult();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public String asText() {
        if (data == null) {
            return "";
        }
        if (data instanceof String) {
            return (String) data;
        }
        return data.toString();
    }
}
