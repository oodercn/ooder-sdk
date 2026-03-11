package net.ooder.skills.capability;

import java.util.Map;
import java.util.Set;

/**
 * 函数定义
 * 
 * <p>扩展支持能力地址的函数定义</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class FunctionDefinition {
    
    private String name;
    private String description;
    private Map<String, Object> parameters;
    
    // ========== 能力地址扩展 (v2.4.0) ==========
    private CapabilityAddress capabilityAddress;  // 能力地址
    private Set<String> supportedOperations;    // 支持的操作
    private Map<String, Object> capabilityConfig; // 能力配置
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    // ========== 能力地址扩展 Getter/Setter ==========
    
    public CapabilityAddress getCapabilityAddress() { return capabilityAddress; }
    public void setCapabilityAddress(CapabilityAddress capabilityAddress) { 
        this.capabilityAddress = capabilityAddress; 
    }
    
    public Set<String> getSupportedOperations() { return supportedOperations; }
    public void setSupportedOperations(Set<String> supportedOperations) { 
        this.supportedOperations = supportedOperations; 
    }
    
    public Map<String, Object> getCapabilityConfig() { return capabilityConfig; }
    public void setCapabilityConfig(Map<String, Object> capabilityConfig) { 
        this.capabilityConfig = capabilityConfig; 
    }
    
    // ========== 便捷方法 ==========
    
    /**
     * 获取能力地址的十六进制字符串
     */
    public String getCapabilityAddressHex() {
        return capabilityAddress != null ? capabilityAddress.getHexAddress() : null;
    }
    
    /**
     * 获取能力分类
     */
    public CapabilityCategory getCapabilityCategory() {
        return capabilityAddress != null ? capabilityAddress.getCategory() : null;
    }
    
    /**
     * 是否支持指定操作
     */
    public boolean supportsOperation(String operation) {
        return supportedOperations != null && supportedOperations.contains(operation);
    }
}
