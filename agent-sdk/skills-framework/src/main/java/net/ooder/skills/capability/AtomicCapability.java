package net.ooder.skills.capability;

import java.util.Map;
import java.util.Set;

/**
 * 原子能力接口
 * 
 * <p>由具体技能实现，提供固定地址的原子能力</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public interface AtomicCapability {
    
    /**
     * 获取能力地址
     * 
     * @return 固定的能力地址
     */
    CapabilityAddress getAddress();
    
    /**
     * 获取支持的操作列表
     * 
     * @return 操作名称集合
     */
    Set<String> getSupportedOperations();
    
    /**
     * 执行操作
     * 
     * @param operation 操作名称
     * @param params 操作参数
     * @param contextRef 上下文引用
     * @return 执行结果
     */
    Result execute(String operation, Map<String, Object> params, ContextReference contextRef);
    
    /**
     * 获取能力名称
     * 
     * @return 能力名称
     */
    default String getName() {
        return getAddress().getName();
    }
    
    /**
     * 是否支持指定操作
     * 
     * @param operation 操作名称
     * @return 是否支持
     */
    default boolean supports(String operation) {
        return getSupportedOperations().contains(operation);
    }
    
    // ========== 内部类 ==========
    
    /**
     * 执行结果
     */
    class Result {
        private boolean success;
        private Object data;
        private String errorCode;
        private String errorMessage;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public static Result success(Object data) {
            Result result = new Result();
            result.setSuccess(true);
            result.setData(data);
            return result;
        }
        
        public static Result failure(String errorCode, String errorMessage) {
            Result result = new Result();
            result.setSuccess(false);
            result.setErrorCode(errorCode);
            result.setErrorMessage(errorMessage);
            return result;
        }
    }
    
    /**
     * 上下文引用
     */
    class ContextReference {
        private String contextId;
        private String tenantId;
        private String userId;
        private Map<String, Object> attributes;
        
        public String getContextId() { return contextId; }
        public void setContextId(String contextId) { this.contextId = contextId; }
        
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public Map<String, Object> getAttributes() { return attributes; }
        public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    }
}
