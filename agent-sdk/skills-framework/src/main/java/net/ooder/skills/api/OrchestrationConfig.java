package net.ooder.skills.api;

import java.util.List;
import java.util.Map;

/**
 * 编排配置
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class OrchestrationConfig {
    
    private String type;                    // 编排类型：STATE_MACHINE, PIPELINE, DAG
    private List<OrchestrationStep> steps;  // 编排步骤
    private Map<String, Object> params;     // 额外参数
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<OrchestrationStep> getSteps() { return steps; }
    public void setSteps(List<OrchestrationStep> steps) { this.steps = steps; }
    
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
