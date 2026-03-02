package net.ooder.skills.api;

/**
 * 兼容性问题详情
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class CompatibilityIssue {
    
    /**
     * 问题级别
     */
    public enum Severity {
        /** 错误 - 阻止安装 */
        ERROR,
        /** 警告 - 可以安装但可能有问题 */
        WARNING,
        /** 信息 - 仅供参考 */
        INFO
    }
    
    private Severity severity;
    private String code;
    private String message;
    private String suggestion;
    
    public CompatibilityIssue() {
    }
    
    public CompatibilityIssue(Severity severity, String code, String message) {
        this.severity = severity;
        this.code = code;
        this.message = message;
    }
    
    // Getters and Setters
    
    public Severity getSeverity() {
        return severity;
    }
    
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getSuggestion() {
        return suggestion;
    }
    
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
    
    /**
     * 创建错误级别的问题
     */
    public static CompatibilityIssue error(String code, String message) {
        return new CompatibilityIssue(Severity.ERROR, code, message);
    }
    
    /**
     * 创建警告级别的问题
     */
    public static CompatibilityIssue warning(String code, String message) {
        return new CompatibilityIssue(Severity.WARNING, code, message);
    }
    
    /**
     * 创建信息级别的问题
     */
    public static CompatibilityIssue info(String code, String message) {
        return new CompatibilityIssue(Severity.INFO, code, message);
    }
    
    @Override
    public String toString() {
        return "CompatibilityIssue{" +
            "severity=" + severity +
            ", code='" + code + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}
