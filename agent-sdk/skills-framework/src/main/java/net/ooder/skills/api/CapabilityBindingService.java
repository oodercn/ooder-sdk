package net.ooder.skills.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 能力动态绑定服务接口
 *
 * 支持能力到场景的动态绑定、条件绑定和解绑
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface CapabilityBindingService {
    
    /**
     * 绑定能力到场景
     *
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 绑定结果
     */
    CompletableFuture<BindingResult> bind(String sceneId, String capabilityId);
    
    /**
     * 绑定能力到场景（带配置）
     *
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @param binding 绑定配置
     * @return 绑定结果
     */
    CompletableFuture<BindingResult> bind(String sceneId, String capabilityId, CapabilityBinding binding);
    
    /**
     * 批量绑定能力
     *
     * @param sceneId 场景ID
     * @param bindings 绑定配置列表
     * @param context 上下文
     * @return 批量绑定结果
     */
    CompletableFuture<BatchBindingResult> bindCapabilities(
        String sceneId, 
        List<CapabilityBinding> bindings,
        Map<String, Object> context
    );
    
    /**
     * 解绑能力
     *
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> unbind(String sceneId, String capabilityId);
    
    /**
     * 批量解绑
     *
     * @param sceneId 场景ID
     * @param capabilityIds 能力ID列表
     * @return 批量解绑结果
     */
    CompletableFuture<BatchUnbindResult> unbindCapabilities(String sceneId, List<String> capabilityIds);
    
    /**
     * 评估条件绑定
     *
     * @param bindings 绑定配置列表
     * @param context 上下文
     * @return 满足条件的绑定列表
     */
    List<CapabilityBinding> evaluateConditions(
        List<CapabilityBinding> bindings,
        Map<String, Object> context
    );
    
    /**
     * 获取场景的能力绑定列表
     *
     * @param sceneId 场景ID
     * @return 绑定列表
     */
    CompletableFuture<List<CapabilityBinding>> getBindings(String sceneId);
    
    /**
     * 获取特定能力的绑定信息
     *
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 绑定信息
     */
    CompletableFuture<CapabilityBinding> getBinding(String sceneId, String capabilityId);
    
    /**
     * 检查能力是否已绑定
     *
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 是否已绑定
     */
    CompletableFuture<Boolean> isBound(String sceneId, String capabilityId);
    
    /**
     * 更新绑定配置
     *
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @param binding 新绑定配置
     * @return 是否成功
     */
    CompletableFuture<Boolean> updateBinding(String sceneId, String capabilityId, CapabilityBinding binding);
    
    /**
     * 激活绑定
     *
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> activateBinding(String sceneId, String capabilityId);
    
    /**
     * 停用绑定
     *
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> deactivateBinding(String sceneId, String capabilityId);
    
    /**
     * 添加绑定监听器
     *
     * @param listener 监听器
     */
    void addBindingListener(BindingListener listener);
    
    /**
     * 移除绑定监听器
     *
     * @param listener 监听器
     */
    void removeBindingListener(BindingListener listener);
    
    // ========== 数据类定义 ==========
    
    /**
     * 能力绑定配置
     */
    class CapabilityBinding {
        private String capabilityId;
        private String skillId;
        private String condition;  // 条件表达式
        private Map<String, Object> params;  // 绑定参数
        private BindingStatus status;
        private long bindTime;
        private String description;
        
        public enum BindingStatus {
            PENDING,    // 待绑定
            BINDING,    // 绑定中
            ACTIVE,     // 活跃
            INACTIVE,   // 停用
            FAILED,     // 失败
            UNBOUND     // 已解绑
        }
        
        // Getters and Setters
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
        public BindingStatus getStatus() { return status; }
        public void setStatus(BindingStatus status) { this.status = status; }
        public long getBindTime() { return bindTime; }
        public void setBindTime(long bindTime) { this.bindTime = bindTime; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * 绑定结果
     */
    class BindingResult {
        private boolean success;
        private String sceneId;
        private String capabilityId;
        private String message;
        private CapabilityBinding binding;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public CapabilityBinding getBinding() { return binding; }
        public void setBinding(CapabilityBinding binding) { this.binding = binding; }
        
        public static BindingResult success(String sceneId, String capabilityId, CapabilityBinding binding) {
            BindingResult result = new BindingResult();
            result.setSuccess(true);
            result.setSceneId(sceneId);
            result.setCapabilityId(capabilityId);
            result.setBinding(binding);
            result.setMessage("绑定成功");
            return result;
        }
        
        public static BindingResult failure(String sceneId, String capabilityId, String message) {
            BindingResult result = new BindingResult();
            result.setSuccess(false);
            result.setSceneId(sceneId);
            result.setCapabilityId(capabilityId);
            result.setMessage(message);
            return result;
        }
    }
    
    /**
     * 批量绑定结果
     */
    class BatchBindingResult {
        private boolean allSuccess;
        private int totalCount;
        private int successCount;
        private int failureCount;
        private List<BindingResult> results;
        
        public BatchBindingResult() {
            this.results = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isAllSuccess() { return allSuccess; }
        public void setAllSuccess(boolean allSuccess) { this.allSuccess = allSuccess; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public List<BindingResult> getResults() { return results; }
        public void setResults(List<BindingResult> results) { this.results = results; }
        
        public void addResult(BindingResult result) {
            if (this.results == null) {
                this.results = new java.util.ArrayList<>();
            }
            this.results.add(result);
            recalculate();
        }
        
        private void recalculate() {
            this.totalCount = results.size();
            this.successCount = (int) results.stream().filter(BindingResult::isSuccess).count();
            this.failureCount = totalCount - successCount;
            this.allSuccess = failureCount == 0;
        }
    }
    
    /**
     * 批量解绑结果
     */
    class BatchUnbindResult {
        private boolean allSuccess;
        private int totalCount;
        private int successCount;
        private int failureCount;
        private List<UnbindResult> results;
        
        public BatchUnbindResult() {
            this.results = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isAllSuccess() { return allSuccess; }
        public void setAllSuccess(boolean allSuccess) { this.allSuccess = allSuccess; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public List<UnbindResult> getResults() { return results; }
        public void setResults(List<UnbindResult> results) { this.results = results; }
    }
    
    /**
     * 解绑结果
     */
    class UnbindResult {
        private boolean success;
        private String sceneId;
        private String capabilityId;
        private String message;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 绑定监听器
     */
    interface BindingListener {
        void onBindingCreated(String sceneId, CapabilityBinding binding);
        void onBindingActivated(String sceneId, String capabilityId);
        void onBindingDeactivated(String sceneId, String capabilityId);
        void onBindingRemoved(String sceneId, String capabilityId);
        void onBindingFailed(String sceneId, String capabilityId, String error);
    }
}
