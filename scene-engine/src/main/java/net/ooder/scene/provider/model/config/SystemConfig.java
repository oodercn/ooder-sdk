package net.ooder.scene.provider.model.config;

import java.util.Map;

public class SystemConfig {
    
    private String environment;
    private String dataPath;
    private String tempPath;
    private long maxMemoryMB;
    private int cpuCores;
    private Map<String, Object> extra;
    
    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public String getDataPath() {
        return dataPath;
    }
    
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
    
    public String getTempPath() {
        return tempPath;
    }
    
    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }
    
    public long getMaxMemoryMB() {
        return maxMemoryMB;
    }
    
    public void setMaxMemoryMB(long maxMemoryMB) {
        this.maxMemoryMB = maxMemoryMB;
    }
    
    public int getCpuCores() {
        return cpuCores;
    }
    
    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
