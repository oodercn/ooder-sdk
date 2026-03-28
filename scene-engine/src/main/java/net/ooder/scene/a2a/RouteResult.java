package net.ooder.scene.a2a;

/**
 * 路由结果
 *
 * <p>封装A2A消息路由的结果</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class RouteResult {
    
    private String messageId;
    private boolean success;
    private String targetAgentId;
    private String errorMessage;
    private long routeTime;
    private RouteStrategy strategy;
    
    public RouteResult() {
        this.routeTime = System.currentTimeMillis();
    }
    
    public RouteResult(String messageId, boolean success) {
        this();
        this.messageId = messageId;
        this.success = success;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getTargetAgentId() {
        return targetAgentId;
    }
    
    public void setTargetAgentId(String targetAgentId) {
        this.targetAgentId = targetAgentId;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public long getRouteTime() {
        return routeTime;
    }
    
    public void setRouteTime(long routeTime) {
        this.routeTime = routeTime;
    }
    
    public RouteStrategy getStrategy() {
        return strategy;
    }
    
    public void setStrategy(RouteStrategy strategy) {
        this.strategy = strategy;
    }
    
    public static RouteResult success(String messageId, String targetAgentId) {
        RouteResult result = new RouteResult(messageId, true);
        result.setTargetAgentId(targetAgentId);
        return result;
    }
    
    public static RouteResult success(String messageId, String targetAgentId, RouteStrategy strategy) {
        RouteResult result = success(messageId, targetAgentId);
        result.setStrategy(strategy);
        return result;
    }
    
    public static RouteResult failure(String messageId, String errorMessage) {
        RouteResult result = new RouteResult(messageId, false);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public static RouteResult noRoute(String messageId) {
        return failure(messageId, "No route found for message");
    }
    
    public static RouteResult noAgentFound(String messageId, String capability) {
        return failure(messageId, "No agent found with capability: " + capability);
    }
    
    public static RouteResult noAgentFoundForRole(String messageId, String role) {
        return failure(messageId, "No agent found with role: " + role);
    }
    
    @Override
    public String toString() {
        return "RouteResult{" +
                "messageId='" + messageId + '\'' +
                ", success=" + success +
                ", targetAgentId='" + targetAgentId + '\'' +
                ", strategy=" + strategy +
                '}';
    }
}
