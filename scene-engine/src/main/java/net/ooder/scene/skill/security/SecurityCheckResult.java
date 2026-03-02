package net.ooder.scene.skill.security;

/**
 * 安全检查结果
 *
 * @author ooder
 * @since 2.3
 */
public class SecurityCheckResult {
    
    /** 是否通过 */
    private boolean allowed;
    
    /** 拒绝原因 */
    private String reason;
    
    /** 错误码 */
    private String errorCode;
    
    public SecurityCheckResult() {}
    
    public SecurityCheckResult(boolean allowed) {
        this.allowed = allowed;
    }
    
    public static SecurityCheckResult allow() {
        return new SecurityCheckResult(true);
    }
    
    public static SecurityCheckResult deny(String reason) {
        SecurityCheckResult result = new SecurityCheckResult(false);
        result.setReason(reason);
        return result;
    }
    
    public static SecurityCheckResult deny(String reason, String errorCode) {
        SecurityCheckResult result = new SecurityCheckResult(false);
        result.setReason(reason);
        result.setErrorCode(errorCode);
        return result;
    }
    
    // Getters and Setters
    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
}
