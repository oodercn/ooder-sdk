package net.ooder.sdk.reach;

import java.util.Map;

public interface ReachProtocol {
    
    String getProtocol();
    
    String getDeviceType();
    
    String getDeviceId();
    
    String getAction();
    
    Map<String, Object> getParams();
    
    String toUri();
    
    static ReachProtocol fromUri(String uri) {
        if (uri == null || !uri.startsWith("REACH://")) {
            throw new IllegalArgumentException("Invalid REACH URI: " + uri);
        }
        
        String remaining = uri.substring(8);
        String[] parts = remaining.split("/");
        
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid REACH URI format: " + uri);
        }
        
        String deviceType = parts[0];
        String deviceId = parts[1];
        
        String actionAndParams = parts[2];
        String action;
        Map<String, Object> params = new java.util.HashMap<>();
        
        int queryIndex = actionAndParams.indexOf('?');
        if (queryIndex >= 0) {
            action = actionAndParams.substring(0, queryIndex);
            String queryString = actionAndParams.substring(queryIndex + 1);
            parseQueryParams(queryString, params);
        } else {
            action = actionAndParams;
        }
        
        return new ReachProtocolImpl(deviceType, deviceId, action, params);
    }
    
    static void parseQueryParams(String queryString, Map<String, Object> params) {
        if (queryString == null || queryString.isEmpty()) return;
        
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int eqIndex = pair.indexOf('=');
            if (eqIndex > 0) {
                String key = pair.substring(0, eqIndex);
                String value = pair.substring(eqIndex + 1);
                params.put(key, value);
            }
        }
    }
    
    static ReachProtocolBuilder builder() {
        return new ReachProtocolBuilder();
    }
}
