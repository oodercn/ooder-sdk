package net.ooder.skills.api.driver;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 意图接收能力接口
 * 
 * 接收用户意图并触发场景启动的驱动能力
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface IntentReceiver {
    
    /**
     * 接收意图
     *
     * @param intent 意图对象
     * @return 意图处理结果
     */
    CompletableFuture<IntentResult> receive(Intent intent);
    
    /**
     * 解析自然语言为意图
     *
     * @param naturalLanguage 自然语言输入
     * @return 解析后的意图结果
     */
    CompletableFuture<IntentResult> parse(String naturalLanguage);
    
    /**
     * 将意图解析为场景能力
     *
     * @param intent 意图对象
     * @return 场景能力信息
     */
    CompletableFuture<SceneCapabilityInfo> resolveCapability(Intent intent);
    
    /**
     * 注册意图处理器
     *
     * @param intentType 意图类型
     * @param handler 处理器
     */
    void registerHandler(String intentType, IntentHandler handler);
    
    /**
     * 注销意图处理器
     *
     * @param intentType 意图类型
     */
    void unregisterHandler(String intentType);
    
    /**
     * 意图对象
     */
    class Intent {
        private String intentId;
        private String intentType;
        private String source;
        private String userId;
        private String rawInput;
        private Map<String, Object> params;
        private double confidence;
        private long timestamp;
        
        // Getters and Setters
        public String getIntentId() { return intentId; }
        public void setIntentId(String intentId) { this.intentId = intentId; }
        public String getIntentType() { return intentType; }
        public void setIntentType(String intentType) { this.intentType = intentType; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getRawInput() { return rawInput; }
        public void setRawInput(String rawInput) { this.rawInput = rawInput; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 意图处理结果
     */
    class IntentResult {
        private boolean success;
        private Intent intent;
        private String message;
        private Map<String, Object> result;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Intent getIntent() { return intent; }
        public void setIntent(Intent intent) { this.intent = intent; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getResult() { return result; }
        public void setResult(Map<String, Object> result) { this.result = result; }
    }
    
    /**
     * 场景能力信息
     */
    class SceneCapabilityInfo {
        private String capabilityId;
        private String capabilityName;
        private double matchScore;
        private Map<String, Object> initParams;
        
        // Getters and Setters
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getCapabilityName() { return capabilityName; }
        public void setCapabilityName(String capabilityName) { this.capabilityName = capabilityName; }
        public double getMatchScore() { return matchScore; }
        public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
        public Map<String, Object> getInitParams() { return initParams; }
        public void setInitParams(Map<String, Object> initParams) { this.initParams = initParams; }
    }
    
    /**
     * 意图处理器
     */
    interface IntentHandler {
        CompletableFuture<IntentResult> handle(Intent intent);
    }
}
