package net.ooder.sdk.api.skill;

import java.io.Serializable;

public class InterfaceDependency implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String interfaceId;
    private String version;
    private boolean required = true;
    private String fallback;
    private String preferredImplementation;
    
    public InterfaceDependency() {}
    
    public InterfaceDependency(String interfaceId) {
        this.interfaceId = interfaceId;
    }
    
    public InterfaceDependency(String interfaceId, String version) {
        this.interfaceId = interfaceId;
        this.version = version;
    }
    
    public static InterfaceDependency create(String interfaceId) {
        return new InterfaceDependency(interfaceId);
    }
    
    public static InterfaceDependency create(String interfaceId, String version) {
        return new InterfaceDependency(interfaceId, version);
    }
    
    public String getInterfaceId() { return interfaceId; }
    public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    
    public String getFallback() { return fallback; }
    public void setFallback(String fallback) { this.fallback = fallback; }
    
    public String getPreferredImplementation() { return preferredImplementation; }
    public void setPreferredImplementation(String preferredImplementation) { 
        this.preferredImplementation = preferredImplementation; 
    }
    
    public InterfaceDependency version(String version) {
        this.version = version;
        return this;
    }
    
    public InterfaceDependency required(boolean required) {
        this.required = required;
        return this;
    }
    
    public InterfaceDependency fallback(String fallback) {
        this.fallback = fallback;
        return this;
    }
    
    public InterfaceDependency preferred(String impl) {
        this.preferredImplementation = impl;
        return this;
    }
    
    @Override
    public String toString() {
        return "InterfaceDependency{" +
                "interfaceId='" + interfaceId + '\'' +
                ", version='" + version + '\'' +
                ", required=" + required +
                ", fallback='" + fallback + '\'' +
                '}';
    }
}
