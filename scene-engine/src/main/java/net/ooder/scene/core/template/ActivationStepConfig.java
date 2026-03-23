package net.ooder.scene.core.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 激活步骤配置
 * 
 * <p>定义场景技能激活流程中的单个步骤</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ActivationStepConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String stepId;           // 步骤ID
    private String stepName;         // 步骤名称
    private String name;             // 步骤名称（兼容字段）
    private String description;      // 步骤描述
    private String stepType;         // 步骤类型（如：CONFIRM_JOIN, CONFIG_CAPABILITY, CONFIRM_CONDITION）
    private int order;               // 执行顺序
    private boolean skippable;       // 是否可跳过
    private boolean required;        // 是否必需
    private boolean autoExecute;     // 是否自动执行
    private String executorType;     // 执行器类型（用于查找对应的ActivationStepExecutor）
    private List<String> privateCapabilities;  // 私有能力列表
    private Map<String, Object> config;  // 步骤配置参数
    
    public ActivationStepConfig() {
        this.skippable = false;
        this.required = true;
    }
    
    public ActivationStepConfig(String stepId, String stepName, String stepType) {
        this();
        this.stepId = stepId;
        this.stepName = stepName;
        this.stepType = stepType;
    }
    
    // Getters and Setters
    
    public String getStepId() {
        return stepId;
    }
    
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }
    
    public String getStepName() {
        return stepName;
    }
    
    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
    
    public String getName() {
        return name != null ? name : stepName;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStepType() {
        return stepType;
    }
    
    public void setStepType(String stepType) {
        this.stepType = stepType;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public boolean isSkippable() {
        return skippable;
    }
    
    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public boolean isAutoExecute() {
        return autoExecute;
    }
    
    public void setAutoExecute(boolean autoExecute) {
        this.autoExecute = autoExecute;
    }
    
    public String getExecutorType() {
        return executorType;
    }
    
    public void setExecutorType(String executorType) {
        this.executorType = executorType;
    }
    
    public List<String> getPrivateCapabilities() {
        return privateCapabilities != null ? privateCapabilities : new ArrayList<>();
    }
    
    public void setPrivateCapabilities(List<String> privateCapabilities) {
        this.privateCapabilities = privateCapabilities;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    @Override
    public String toString() {
        return "ActivationStepConfig{" +
                "stepId='" + stepId + '\'' +
                ", stepName='" + stepName + '\'' +
                ", stepType='" + stepType + '\'' +
                ", order=" + order +
                ", required=" + required +
                '}';
    }
}
