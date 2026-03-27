package net.ooder.scene.a2a;

/**
 * A2A 响应
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class A2AResponse {
    
    private String requestId;
    private String fromAgentId;
    private String toAgentId;
    
    private boolean success;
    private Object result;
    private String errorMessage;
    private String errorCode;
    
    private long responseTime;
    private long createdAt;
    
    public A2AResponse() {
        this.createdAt = System.currentTimeMillis();
    }
    
    public A2AResponse(String requestId, String fromAgentId) {
        this();
        this.requestId = requestId;
        this.fromAgentId = fromAgentId;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
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
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
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
    
    public long getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public static A2AResponse success(String requestId, String fromAgentId, Object result) {
        A2AResponse response = new A2AResponse(requestId, fromAgentId);
        response.setSuccess(true);
        response.setResult(result);
        response.setResponseTime(System.currentTimeMillis());
        return response;
    }
    
    public static A2AResponse failure(String requestId, String fromAgentId, String errorMessage) {
        A2AResponse response = new A2AResponse(requestId, fromAgentId);
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setResponseTime(System.currentTimeMillis());
        return response;
    }
    
    public static A2AResponse failure(String requestId, String fromAgentId, String errorCode, String errorMessage) {
        A2AResponse response = failure(requestId, fromAgentId, errorMessage);
        response.setErrorCode(errorCode);
        return response;
    }
    
    public A2AMessage toA2AMessage() {
        A2AMessage message = A2AMessage.builder()
                .messageId(java.util.UUID.randomUUID().toString().replace("-", ""))
                .from(fromAgentId)
                .to(toAgentId)
                .type(A2AMessageType.TASK_RESPONSE)
                .build();
        
        message.setHeader("requestId", requestId);
        message.setPayload(java.util.Map.of(
                "success", success,
                "result", result != null ? result : "",
                "errorMessage", errorMessage != null ? errorMessage : "",
                "responseTime", responseTime
        ));
        
        return message;
    }
    
    @Override
    public String toString() {
        return "A2AResponse{" +
                "requestId='" + requestId + '\'' +
                ", fromAgentId='" + fromAgentId + '\'' +
                ", success=" + success +
                '}';
    }
}
