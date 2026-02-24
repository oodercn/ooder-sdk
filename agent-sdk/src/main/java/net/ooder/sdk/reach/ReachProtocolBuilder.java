package net.ooder.sdk.reach;

import java.util.HashMap;
import java.util.Map;

public class ReachProtocolBuilder {
    
    private String deviceType;
    private String deviceId;
    private String action;
    private Map<String, Object> params = new HashMap<>();
    
    public ReachProtocolBuilder deviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }
    
    public ReachProtocolBuilder deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    public ReachProtocolBuilder action(String action) {
        this.action = action;
        return this;
    }
    
    public ReachProtocolBuilder params(Map<String, Object> params) {
        this.params = params != null ? params : new HashMap<>();
        return this;
    }
    
    public ReachProtocolBuilder addParam(String key, Object value) {
        this.params.put(key, value);
        return this;
    }
    
    public ReachProtocol build() {
        if (deviceType == null || deviceId == null || action == null) {
            throw new IllegalArgumentException("deviceType, deviceId and action are required");
        }
        return new ReachProtocolImpl(deviceType, deviceId, action, params);
    }
}
