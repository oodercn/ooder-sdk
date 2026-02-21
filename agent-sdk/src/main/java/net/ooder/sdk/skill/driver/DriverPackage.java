package net.ooder.sdk.skill.driver;

import java.util.List;

public class DriverPackage {
    private String skillId;
    private String version;
    private InterfaceDefinition interfaceDef;
    private Class<?> proxyClass;
    private Class<?> fallbackClass;
    private List<String> limitations;
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public InterfaceDefinition getInterfaceDef() { return interfaceDef; }
    public void setInterfaceDef(InterfaceDefinition interfaceDef) { this.interfaceDef = interfaceDef; }
    
    public Class<?> getProxyClass() { return proxyClass; }
    public void setProxyClass(Class<?> proxyClass) { this.proxyClass = proxyClass; }
    
    public Class<?> getFallbackClass() { return fallbackClass; }
    public void setFallbackClass(Class<?> fallbackClass) { this.fallbackClass = fallbackClass; }
    
    public List<String> getLimitations() { return limitations; }
    public void setLimitations(List<String> limitations) { this.limitations = limitations; }
    
    public boolean hasFallback() {
        return fallbackClass != null;
    }
    
    public Object createProxy(EndpointConfig config) {
        // 创建远程调用代理
        return null;
    }
    
    public Object createFallback() {
        // 创建降级实现实例
        try {
            return fallbackClass != null ? fallbackClass.newInstance() : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create fallback instance", e);
        }
    }
}