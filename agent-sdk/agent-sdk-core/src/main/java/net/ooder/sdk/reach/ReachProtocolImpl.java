package net.ooder.sdk.reach;

import java.util.Collections;
import java.util.Map;

class ReachProtocolImpl implements ReachProtocol {
    
    private final String deviceType;
    private final String deviceId;
    private final String action;
    private final Map<String, Object> params;
    
    ReachProtocolImpl(String deviceType, String deviceId, String action, Map<String, Object> params) {
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.action = action;
        this.params = Collections.unmodifiableMap(params);
    }
    
    @Override
    public String getProtocol() {
        return "REACH";
    }
    
    @Override
    public String getDeviceType() {
        return deviceType;
    }
    
    @Override
    public String getDeviceId() {
        return deviceId;
    }
    
    @Override
    public String getAction() {
        return action;
    }
    
    @Override
    public Map<String, Object> getParams() {
        return params;
    }
    
    @Override
    public String toUri() {
        StringBuilder uri = new StringBuilder("REACH://");
        uri.append(deviceType).append("/");
        uri.append(deviceId).append("/");
        uri.append(action);
        
        if (!params.isEmpty()) {
            uri.append("?");
            boolean first = true;
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (!first) uri.append("&");
                uri.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
        }
        
        return uri.toString();
    }
    
    @Override
    public String toString() {
        return toUri();
    }
}
