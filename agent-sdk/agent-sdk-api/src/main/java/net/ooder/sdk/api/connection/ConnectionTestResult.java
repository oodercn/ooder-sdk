package net.ooder.sdk.api.connection;

/**
 * 连接测试结果
 * 包含连接测试的详细结果信息
 *
 * @author ooder
 * @since 2.3
 */
public class ConnectionTestResult {
    
    /** 是否成功 */
    private boolean success;
    
    /** 延迟(毫秒) */
    private int latency;
    
    /** 结果消息 */
    private String message;
    
    /** 错误信息(如果失败) */
    private String errorMessage;
    
    /** 服务器版本信息 */
    private String serverVersion;
    
    /**
     * 默认构造函数
     */
    public ConnectionTestResult() {}
    
    /**
     * 创建成功结果
     * @param latency 延迟
     * @param message 消息
     * @return 成功结果
     */
    public static ConnectionTestResult success(int latency, String message) {
        ConnectionTestResult result = new ConnectionTestResult();
        result.setSuccess(true);
        result.setLatency(latency);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 创建失败结果
     * @param errorMessage 错误消息
     * @return 失败结果
     */
    public static ConnectionTestResult failure(String errorMessage) {
        ConnectionTestResult result = new ConnectionTestResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setMessage("Connection failed: " + errorMessage);
        return result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public int getLatency() {
        return latency;
    }
    
    public void setLatency(int latency) {
        this.latency = latency;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getServerVersion() {
        return serverVersion;
    }
    
    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }
}
