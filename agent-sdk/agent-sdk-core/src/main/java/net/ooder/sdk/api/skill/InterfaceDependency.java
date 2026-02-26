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
    
    public boolean hasVersionConstraint() {
        return version != null && !version.isEmpty();
    }
    
    public boolean versionSatisfies(String actualVersion) {
        if (!hasVersionConstraint()) {
            return true;
        }
        
        if (actualVersion == null) {
            return false;
        }
        
        if (version.startsWith(">=")) {
            String minVersion = version.substring(2).trim();
            return compareVersions(actualVersion, minVersion) >= 0;
        } else if (version.startsWith(">")) {
            String minVersion = version.substring(1).trim();
            return compareVersions(actualVersion, minVersion) > 0;
        } else if (version.startsWith("<=")) {
            String maxVersion = version.substring(2).trim();
            return compareVersions(actualVersion, maxVersion) <= 0;
        } else if (version.startsWith("<")) {
            String maxVersion = version.substring(1).trim();
            return compareVersions(actualVersion, maxVersion) < 0;
        } else if (version.startsWith("^")) {
            String baseVersion = version.substring(1).trim();
            return isCompatibleVersion(actualVersion, baseVersion);
        } else {
            return version.equals(actualVersion);
        }
    }
    
    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        
        int maxLen = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLen; i++) {
            int num1 = i < parts1.length ? parseVersionPart(parts1[i]) : 0;
            int num2 = i < parts2.length ? parseVersionPart(parts2[i]) : 0;
            
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }
    
    private int parseVersionPart(String part) {
        try {
            return Integer.parseInt(part.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private boolean isCompatibleVersion(String actualVersion, String baseVersion) {
        String[] actualParts = actualVersion.split("\\.");
        String[] baseParts = baseVersion.split("\\.");
        
        if (actualParts.length < 1 || baseParts.length < 1) {
            return false;
        }
        
        return actualParts[0].equals(baseParts[0]);
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
