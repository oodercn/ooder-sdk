package net.ooder.sdk.service.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatConfig {
    
    private DeviceType deviceType;
    private int normalInterval = 30000;
    private int sleepInterval = 60000;
    private int offlineThreshold = 3;
    private int timeout = 5000;
    
    public enum DeviceType {
        FIXED("fixed", "固定设备 (PC、NAS)", 30000, -1, 3),
        MOBILE("mobile", "移动设备 (手机)", 30000, 60000, 3),
        BATTERY("battery", "电池设备 (传感器)", 60000, 120000, 3);
        
        private final String code;
        private final String description;
        private final int normalInterval;
        private final int sleepInterval;
        private final int offlineThreshold;
        
        DeviceType(String code, String description, int normalInterval, 
                   int sleepInterval, int offlineThreshold) {
            this.code = code;
            this.description = description;
            this.normalInterval = normalInterval;
            this.sleepInterval = sleepInterval;
            this.offlineThreshold = offlineThreshold;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
        public int getNormalInterval() { return normalInterval; }
        public int getSleepInterval() { return sleepInterval; }
        public int getOfflineThreshold() { return offlineThreshold; }
        
        public static DeviceType fromCode(String code) {
            for (DeviceType type : values()) {
                if (type.code.equalsIgnoreCase(code)) {
                    return type;
                }
            }
            return FIXED;
        }
    }
    
    public HeartbeatConfig() {
        this.deviceType = DeviceType.FIXED;
        applyDeviceTypeDefaults();
    }
    
    public HeartbeatConfig(DeviceType deviceType) {
        this.deviceType = deviceType;
        applyDeviceTypeDefaults();
    }
    
    private void applyDeviceTypeDefaults() {
        this.normalInterval = deviceType.getNormalInterval();
        this.sleepInterval = deviceType.getSleepInterval();
        this.offlineThreshold = deviceType.getOfflineThreshold();
    }
    
    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { 
        this.deviceType = deviceType; 
        applyDeviceTypeDefaults();
    }
    
    public int getNormalInterval() { return normalInterval; }
    public void setNormalInterval(int normalInterval) { this.normalInterval = normalInterval; }
    
    public int getSleepInterval() { return sleepInterval; }
    public void setSleepInterval(int sleepInterval) { this.sleepInterval = sleepInterval; }
    
    public int getOfflineThreshold() { return offlineThreshold; }
    public void setOfflineThreshold(int offlineThreshold) { this.offlineThreshold = offlineThreshold; }
    
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    
    public boolean hasSleepMode() {
        return sleepInterval > 0;
    }
    
    public int getCurrentInterval(boolean isSleeping) {
        if (isSleeping && hasSleepMode()) {
            return sleepInterval;
        }
        return normalInterval;
    }
    
    @Override
    public String toString() {
        return String.format("HeartbeatConfig{type=%s, normal=%dms, sleep=%dms, threshold=%d}",
            deviceType.getCode(), normalInterval, sleepInterval, offlineThreshold);
    }
}
