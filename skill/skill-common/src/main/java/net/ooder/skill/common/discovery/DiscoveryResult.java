package net.ooder.skill.common.discovery;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryResult {
    
    private List<CapabilityDTO> capabilities = new ArrayList<>();
    private List<CapabilityDTO> scenes = new ArrayList<>();
    private long scanTime;
    private boolean fromCache;
    private String errorMessage;
    private DiscoveryMethod method;
    private String source;
    
    public DiscoveryResult() {}
    
    public static DiscoveryResult success(List<CapabilityDTO> capabilities, DiscoveryMethod method, String source) {
        DiscoveryResult result = new DiscoveryResult();
        result.setCapabilities(capabilities);
        result.setMethod(method);
        result.setSource(source);
        result.setScanTime(System.currentTimeMillis());
        return result;
    }
    
    public static DiscoveryResult error(String errorMessage, DiscoveryMethod method) {
        DiscoveryResult result = new DiscoveryResult();
        result.setErrorMessage(errorMessage);
        result.setMethod(method);
        result.setScanTime(System.currentTimeMillis());
        return result;
    }
    
    public List<CapabilityDTO> getCapabilities() { return capabilities; }
    public void setCapabilities(List<CapabilityDTO> capabilities) { 
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
    }
    
    public List<CapabilityDTO> getScenes() { return scenes; }
    public void setScenes(List<CapabilityDTO> scenes) { 
        this.scenes = scenes != null ? scenes : new ArrayList<>();
    }
    
    public long getScanTime() { return scanTime; }
    public void setScanTime(long scanTime) { this.scanTime = scanTime; }
    
    public boolean isFromCache() { return fromCache; }
    public void setFromCache(boolean fromCache) { this.fromCache = fromCache; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public DiscoveryMethod getMethod() { return method; }
    public void setMethod(DiscoveryMethod method) { this.method = method; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public boolean isSuccess() {
        return errorMessage == null || errorMessage.isEmpty();
    }
    
    public int getTotalCount() {
        return capabilities.size() + scenes.size();
    }
    
    public List<CapabilityDTO> getAllCapabilities() {
        List<CapabilityDTO> all = new ArrayList<>(capabilities);
        all.addAll(scenes);
        return all;
    }
}
