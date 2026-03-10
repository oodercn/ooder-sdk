package net.ooder.skills.api;

import java.util.List;

/**
 * 协作声明
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class CollaborationDeclaration {
    private boolean externallyAccessible;
    private List<String> exposedCapabilities;
    private List<ExternalDependency> externalDependencies;
    
    public boolean isExternallyAccessible() { return externallyAccessible; }
    public void setExternallyAccessible(boolean externallyAccessible) { this.externallyAccessible = externallyAccessible; }
    
    public List<String> getExposedCapabilities() { return exposedCapabilities; }
    public void setExposedCapabilities(List<String> exposedCapabilities) { this.exposedCapabilities = exposedCapabilities; }
    
    public List<ExternalDependency> getExternalDependencies() { return externalDependencies; }
    public void setExternalDependencies(List<ExternalDependency> externalDependencies) { this.externalDependencies = externalDependencies; }
}
