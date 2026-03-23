package net.ooder.skills.api;

import java.util.List;
import java.util.Map;

/**
 * 激活步骤配置
 * 定义场景激活时的步骤配置
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ActivationStepConfig {
    
    private String stepId;
    private String name;
    private String stepType;
    private boolean required;
    private boolean autoExecute;
    private boolean skippable;
    private Map<String, Object> params;
    private List<String> privateCapabilities;
    
    public String getStepId() {
        return stepId;
    }
    
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStepType() {
        return stepType;
    }
    
    public void setStepType(String stepType) {
        this.stepType = stepType;
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
    
    public boolean isSkippable() {
        return skippable;
    }
    
    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }
    
    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    
    public List<String> getPrivateCapabilities() {
        return privateCapabilities;
    }
    
    public void setPrivateCapabilities(List<String> privateCapabilities) {
        this.privateCapabilities = privateCapabilities;
    }
}
