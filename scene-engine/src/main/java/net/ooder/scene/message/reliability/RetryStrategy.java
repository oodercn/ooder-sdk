package net.ooder.scene.message.reliability;

/**
 * 重试策略枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum RetryStrategy {
    
    IMMEDIATE("immediate", "立即重试"),
    FIXED_INTERVAL("fixed_interval", "固定间隔"),
    LINEAR_BACKOFF("linear_backoff", "线性退避"),
    EXPONENTIAL_BACKOFF("exponential_backoff", "指数退避");
    
    private final String code;
    private final String description;
    
    RetryStrategy(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static RetryStrategy fromCode(String code) {
        for (RetryStrategy strategy : values()) {
            if (strategy.code.equalsIgnoreCase(code)) {
                return strategy;
            }
        }
        return EXPONENTIAL_BACKOFF;
    }
}
