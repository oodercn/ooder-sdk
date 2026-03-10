package net.ooder.skills.api;

import java.util.List;

/**
 * 协作配置
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class CollaborationConfig {
    
    private boolean externallyAccessible;       // 是否对外可访问
    private List<String> exposedCapabilities;   // 暴露的能力ID列表
    private List<ExternalDependency> externalDependencies;  // 外部依赖
    
    public boolean isExternallyAccessible() { return externallyAccessible; }
    public void setExternallyAccessible(boolean externallyAccessible) { this.externallyAccessible = externallyAccessible; }
    
    public List<String> getExposedCapabilities() { return exposedCapabilities; }
    public void setExposedCapabilities(List<String> exposedCapabilities) { this.exposedCapabilities = exposedCapabilities; }
    
    public List<ExternalDependency> getExternalDependencies() { return externalDependencies; }
    public void setExternalDependencies(List<ExternalDependency> externalDependencies) { this.externalDependencies = externalDependencies; }
}
