package net.ooder.scene.core.activation.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 激活上下文
 * 
 * <p>在激活流程中传递和共享的上下文数据，包含：</p>
 * <ul>
 *   <li>用户信息和角色</li>
 *   <li>配置参数</li>
 *   <li>步骤间共享数据</li>
 *   <li>环境变量</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ActivationContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String contextId;              // 上下文ID
    private String activationId;           // 激活ID
    private String sceneId;                // 场景ID
    private String templateId;             // 模板ID
    private String userId;                 // 用户ID
    private String userName;               // 用户名
    private String roleId;                 // 角色ID
    private String roleName;               // 角色名
    
    private Map<String, Object> config;           // 配置参数
    private Map<String, Object> variables;        // 变量
    private Map<String, Object> stepOutput;       // 步骤输出缓存
    private Map<String, Object> environment;      // 环境变量
    private Map<String, Object> metadata;         // 元数据
    
    private long createdAt;                // 创建时间
    private long updatedAt;                // 更新时间
    private String currentStepId;          // 当前步骤ID
    
    public ActivationContext() {
        this.contextId = generateContextId();
        this.config = new HashMap<>();
        this.variables = new HashMap<>();
        this.stepOutput = new HashMap<>();
        this.environment = new HashMap<>();
        this.metadata = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }
    
    public ActivationContext(String activationId, String sceneId, String userId) {
        this();
        this.activationId = activationId;
        this.sceneId = sceneId;
        this.userId = userId;
    }
    
    private static String generateContextId() {
        return "ctx-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 设置配置参数
     */
    public void setConfigValue(String key, Object value) {
        config.put(key, value);
        touch();
    }
    
    /**
     * 获取配置参数
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String key) {
        return (T) config.get(key);
    }
    
    /**
     * 获取配置参数（带默认值）
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String key, T defaultValue) {
        Object value = config.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * 设置变量
     */
    public void setVariable(String key, Object value) {
        variables.put(key, value);
        touch();
    }
    
    /**
     * 获取变量
     */
    @SuppressWarnings("unchecked")
    public <T> T getVariable(String key) {
        return (T) variables.get(key);
    }
    
    /**
     * 设置步骤输出
     */
    public void setStepOutput(String stepId, Object output) {
        stepOutput.put(stepId, output);
        touch();
    }
    
    /**
     * 获取步骤输出
     */
    @SuppressWarnings("unchecked")
    public <T> T getStepOutput(String stepId) {
        return (T) stepOutput.get(stepId);
    }
    
    /**
     * 获取上一步输出
     */
    @SuppressWarnings("unchecked")
    public <T> T getPreviousStepOutput(String currentStepId) {
        String prevStepId = "step-" + (Integer.parseInt(currentStepId.replace("step-", "")) - 1);
        return (T) stepOutput.get(prevStepId);
    }
    
    /**
     * 设置环境变量
     */
    public void setEnvironment(String key, Object value) {
        environment.put(key, value);
        touch();
    }
    
    /**
     * 获取环境变量
     */
    @SuppressWarnings("unchecked")
    public <T> T getEnvironment(String key) {
        return (T) environment.get(key);
    }
    
    /**
     * 合并上下文
     */
    public void merge(ActivationContext other) {
        if (other == null) {
            return;
        }
        if (other.config != null) {
            this.config.putAll(other.config);
        }
        if (other.variables != null) {
            this.variables.putAll(other.variables);
        }
        if (other.environment != null) {
            this.environment.putAll(other.environment);
        }
        touch();
    }
    
    /**
     * 清除敏感数据
     */
    public void clearSensitiveData() {
        variables.remove("password");
        variables.remove("secret");
        variables.remove("token");
        variables.remove("apiKey");
        config.remove("password");
        config.remove("secret");
        config.remove("token");
        touch();
    }
    
    /**
     * 创建快照
     */
    public ActivationContext createSnapshot() {
        ActivationContext snapshot = new ActivationContext();
        snapshot.setContextId(this.contextId + "-snapshot");
        snapshot.setActivationId(this.activationId);
        snapshot.setSceneId(this.sceneId);
        snapshot.setTemplateId(this.templateId);
        snapshot.setUserId(this.userId);
        snapshot.setUserName(this.userName);
        snapshot.setRoleId(this.roleId);
        snapshot.setRoleName(this.roleName);
        snapshot.setConfig(new HashMap<>(this.config));
        snapshot.setVariables(new HashMap<>(this.variables));
        snapshot.setStepOutput(new HashMap<>(this.stepOutput));
        snapshot.setEnvironment(new HashMap<>(this.environment));
        snapshot.setMetadata(new HashMap<>(this.metadata));
        snapshot.setCreatedAt(this.createdAt);
        snapshot.setUpdatedAt(System.currentTimeMillis());
        return snapshot;
    }
    
    private void touch() {
        this.updatedAt = System.currentTimeMillis();
    }
    
    // Getters and Setters
    
    public String getContextId() {
        return contextId;
    }
    
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }
    
    public String getActivationId() {
        return activationId;
    }
    
    public void setActivationId(String activationId) {
        this.activationId = activationId;
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getRoleId() {
        return roleId;
    }
    
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config != null ? config : new HashMap<>();
    }
    
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables != null ? variables : new HashMap<>();
    }
    
    public Map<String, Object> getStepOutput() {
        return stepOutput;
    }
    
    public void setStepOutput(Map<String, Object> stepOutput) {
        this.stepOutput = stepOutput != null ? stepOutput : new HashMap<>();
    }
    
    public Map<String, Object> getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(Map<String, Object> environment) {
        this.environment = environment != null ? environment : new HashMap<>();
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCurrentStepId() {
        return currentStepId;
    }
    
    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }
    
    @Override
    public String toString() {
        return "ActivationContext{" +
                "contextId='" + contextId + '\'' +
                ", activationId='" + activationId + '\'' +
                ", sceneId='" + sceneId + '\'' +
                ", userId='" + userId + '\'' +
                ", roleId='" + roleId + '\'' +
                ", currentStepId='" + currentStepId + '\'' +
                '}';
    }
}
