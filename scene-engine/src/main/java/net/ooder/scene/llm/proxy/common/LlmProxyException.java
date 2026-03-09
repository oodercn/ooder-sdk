package net.ooder.scene.llm.proxy.common;

/**
 * LLM代理层异常基类
 */
public class LlmProxyException extends Exception {
    
    private final String errorCode;
    
    public LlmProxyException(String message) {
        super(message);
        this.errorCode = "PROXY_ERROR";
    }
    
    public LlmProxyException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public LlmProxyException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PROXY_ERROR";
    }
    
    public LlmProxyException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
