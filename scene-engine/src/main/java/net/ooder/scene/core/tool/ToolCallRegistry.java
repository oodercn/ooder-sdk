package net.ooder.scene.core.tool;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 工具调用注册中心接口
 *
 * <p>管理工具的注册、发现和调用。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface ToolCallRegistry {

    /**
     * 注册工具
     *
     * @param toolDef 工具定义
     * @return 注册结果
     */
    ToolRegistration registerTool(ToolDefinition toolDef);

    /**
     * 注销工具
     *
     * @param toolId 工具ID
     */
    void unregisterTool(String toolId);

    /**
     * 获取工具定义
     *
     * @param toolId 工具ID
     * @return 工具定义
     */
    ToolDefinition getToolDefinition(String toolId);

    /**
     * 获取场景工具列表
     *
     * @param sceneId 场景ID
     * @return 工具列表
     */
    List<ToolDefinition> getSceneTools(String sceneId);

    /**
     * 发现工具
     *
     * @param query 查询条件
     * @return 工具列表
     */
    List<ToolDefinition> discoverTools(ToolQuery query);

    /**
     * 执行工具
     *
     * @param request 执行请求
     * @return 执行结果
     */
    CompletableFuture<ToolResult> executeTool(ToolExecutionRequest request);

    /**
     * 批量执行工具
     *
     * @param requests 执行请求列表
     * @return 执行结果列表
     */
    CompletableFuture<List<ToolResult>> executeToolsBatch(List<ToolExecutionRequest> requests);

    /**
     * 获取工具执行状态
     *
     * @param executionId 执行ID
     * @return 执行状态
     */
    ToolExecutionStatus getExecutionStatus(String executionId);

    /**
     * 取消工具执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    boolean cancelExecution(String executionId);

    /**
     * 订阅工具事件
     *
     * @param toolId 工具ID
     * @param listener 事件监听器
     * @return 订阅ID
     */
    String subscribeToolEvent(String toolId, ToolEventListener listener);

    /**
     * 取消订阅
     *
     * @param subscriptionId 订阅ID
     */
    void unsubscribeToolEvent(String subscriptionId);

    /**
     * 工具定义
     */
    class ToolDefinition {
        private String toolId;
        private String sceneId;
        private String skillId;
        private String name;
        private String description;
        private String category;
        private Map<String, Object> parametersSchema;
        private Map<String, Object> returnsSchema;
        private String handlerClass;
        private boolean enabled;
        private int timeout;
        private int maxRetries;

        public String getToolId() { return toolId; }
        public void setToolId(String toolId) { this.toolId = toolId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Map<String, Object> getParametersSchema() { return parametersSchema; }
        public void setParametersSchema(Map<String, Object> parametersSchema) { this.parametersSchema = parametersSchema; }
        public Map<String, Object> getReturnsSchema() { return returnsSchema; }
        public void setReturnsSchema(Map<String, Object> returnsSchema) { this.returnsSchema = returnsSchema; }
        public String getHandlerClass() { return handlerClass; }
        public void setHandlerClass(String handlerClass) { this.handlerClass = handlerClass; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    }

    /**
     * 工具注册结果
     */
    class ToolRegistration {
        private String toolId;
        private boolean success;
        private String errorMessage;

        public String getToolId() { return toolId; }
        public void setToolId(String toolId) { this.toolId = toolId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 工具查询
     */
    class ToolQuery {
        private String sceneId;
        private String skillId;
        private String category;
        private String namePattern;
        private List<String> tags;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getNamePattern() { return namePattern; }
        public void setNamePattern(String namePattern) { this.namePattern = namePattern; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }

    /**
     * 工具执行请求
     */
    class ToolExecutionRequest {
        private String executionId;
        private String toolId;
        private String sceneId;
        private String userId;
        private Map<String, Object> parameters;
        private int timeout;
        private Map<String, Object> context;

        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        public String getToolId() { return toolId; }
        public void setToolId(String toolId) { this.toolId = toolId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }

    /**
     * 工具执行结果
     */
    class ToolResult {
        private String executionId;
        private String toolId;
        private boolean success;
        private Object result;
        private String error;
        private long duration;
        private int retryCount;

        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        public String getToolId() { return toolId; }
        public void setToolId(String toolId) { this.toolId = toolId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    }

    /**
     * 工具执行状态
     */
    class ToolExecutionStatus {
        private String executionId;
        private String toolId;
        private ExecutionPhase phase;
        private long startTime;
        private long elapsedTime;
        private String message;

        public enum ExecutionPhase {
            PENDING, RUNNING, COMPLETED, FAILED, CANCELLED, TIMEOUT
        }

        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        public String getToolId() { return toolId; }
        public void setToolId(String toolId) { this.toolId = toolId; }
        public ExecutionPhase getPhase() { return phase; }
        public void setPhase(ExecutionPhase phase) { this.phase = phase; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getElapsedTime() { return elapsedTime; }
        public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * 工具事件监听器
     */
    interface ToolEventListener {
        void onToolEvent(ToolEvent event);
    }

    /**
     * 工具事件
     */
    class ToolEvent {
        private String toolId;
        private String executionId;
        private ToolEventType eventType;
        private String message;
        private long timestamp;

        public enum ToolEventType {
            REGISTERED, UNREGISTERED, EXECUTION_STARTED, EXECUTION_COMPLETED, EXECUTION_FAILED, EXECUTION_CANCELLED
        }

        public String getToolId() { return toolId; }
        public void setToolId(String toolId) { this.toolId = toolId; }
        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        public ToolEventType getEventType() { return eventType; }
        public void setEventType(ToolEventType eventType) { this.eventType = eventType; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
