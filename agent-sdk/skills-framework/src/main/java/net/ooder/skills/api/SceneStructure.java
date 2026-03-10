package net.ooder.skills.api;

import java.util.List;

/**
 * 场景结构
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class SceneStructure {
    
    private String type;                    // 结构类型：SEQUENTIAL, PARALLEL, STATE_MACHINE
    private List<String> internalCapabilities;  // 内部能力ID列表
    private List<String> childSkills;       // 子技能ID列表
    private OrchestrationConfig orchestration;  // 编排配置
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<String> getInternalCapabilities() { return internalCapabilities; }
    public void setInternalCapabilities(List<String> internalCapabilities) { this.internalCapabilities = internalCapabilities; }
    
    public List<String> getChildSkills() { return childSkills; }
    public void setChildSkills(List<String> childSkills) { this.childSkills = childSkills; }
    
    public OrchestrationConfig getOrchestration() { return orchestration; }
    public void setOrchestration(OrchestrationConfig orchestration) { this.orchestration = orchestration; }
}
