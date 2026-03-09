package net.ooder.sdk.agent.capability;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 能力调用 API
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface CapabilityInvocationApi {

    /**
     * 发现场景中的能力
     * @param sceneId 场景ID
     * @return 能力列表
     */
    CompletableFuture<List<CapabilityInfo>> discoverCapabilities(String sceneId);

    /**
     * 调用能力
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @param params 参数
     * @return 调用结果
     */
    CompletableFuture<CapabilityResult> invokeCapability(String sceneId, String capabilityId, Map<String, Object> params);

    /**
     * 异步调用能力
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @param params 参数
     * @param callback 回调
     * @return 任务ID
     */
    CompletableFuture<String> invokeCapabilityAsync(String sceneId, String capabilityId, Map<String, Object> params, CapabilityCallback callback);

    /**
     * 批量调用能力
     * @param sceneId 场景ID
     * @param invocations 调用列表
     * @return 结果列表
     */
    CompletableFuture<List<CapabilityResult>> invokeCapabilitiesBatch(String sceneId, List<CapabilityInvocation> invocations);

    /**
     * 获取能力状态
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 能力状态
     */
    CompletableFuture<CapabilityStatus> getCapabilityStatus(String sceneId, String capabilityId);

    /**
     * 注册本地能力
     * @param sceneId 场景ID
     * @param capability 能力信息
     * @return 是否成功
     */
    CompletableFuture<Boolean> registerCapability(String sceneId, CapabilityInfo capability);

    /**
     * 注销能力
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> unregisterCapability(String sceneId, String capabilityId);

    /**
     * 能力信息
     */
    class CapabilityInfo {
        private String id;
        private String name;
        private String description;
        private String version;
        private List<ParameterDef> parameters;
        private String returnType;
        private Map<String, Object> metadata;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public List<ParameterDef> getParameters() { return parameters; }
        public void setParameters(List<ParameterDef> parameters) { this.parameters = parameters; }
        public String getReturnType() { return returnType; }
        public void setReturnType(String returnType) { this.returnType = returnType; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * 参数定义
     */
    class ParameterDef {
        private String name;
        private String type;
        private boolean required;
        private String description;
        private Object defaultValue;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
    }

    /**
     * 能力调用
     */
    class CapabilityInvocation {
        private String capabilityId;
        private Map<String, Object> params;

        // Getters and Setters
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }

    /**
     * 能力调用结果
     */
    class CapabilityResult {
        private boolean success;
        private Object data;
        private String error;
        private long executionTime;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    }

    /**
     * 能力状态
     */
    class CapabilityStatus {
        private String id;
        private String state;
        private int activeInvocations;
        private long lastUsed;
        private Map<String, Object> metrics;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public int getActiveInvocations() { return activeInvocations; }
        public void setActiveInvocations(int activeInvocations) { this.activeInvocations = activeInvocations; }
        public long getLastUsed() { return lastUsed; }
        public void setLastUsed(long lastUsed) { this.lastUsed = lastUsed; }
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    }

    /**
     * 能力调用回调
     */
    interface CapabilityCallback {
        void onComplete(CapabilityResult result);
        void onError(String error);
    }
}
