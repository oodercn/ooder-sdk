package net.ooder.skills.api;

import java.util.Map;

/**
 * 编排步骤
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class OrchestrationStep {
    
    private String id;                      // 步骤ID
    private String name;                    // 步骤名称
    private String capabilityId;            // 能力ID
    private String type;                    // 步骤类型：TASK, CONDITION, PARALLEL
    private Map<String, Object> params;     // 步骤参数
    private String nextStep;                // 下一步ID
    private String condition;               // 条件表达式（CONDITION类型）
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
    
    public String getNextStep() { return nextStep; }
    public void setNextStep(String nextStep) { this.nextStep = nextStep; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
