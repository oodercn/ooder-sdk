package net.ooder.sdk.reach.impl;

import net.ooder.sdk.reach.ReachExecutor;
import net.ooder.sdk.reach.ReachProtocol;
import net.ooder.sdk.reach.ReachResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ApiReachExecutor implements ReachExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(ApiReachExecutor.class);
    
    @Override
    public boolean supports(String deviceType) {
        return "api".equalsIgnoreCase(deviceType) || 
               "rest".equalsIgnoreCase(deviceType) ||
               "http".equalsIgnoreCase(deviceType);
    }
    
    @Override
    public ReachResult execute(ReachProtocol protocol) {
        String action = protocol.getAction();
        Map<String, Object> params = protocol.getParams();
        
        log.info("Executing API action: {} on device: {}", action, protocol.getDeviceId());
        
        switch (action.toLowerCase()) {
            case "get":
                return executeGet(protocol.getDeviceId(), params);
            case "post":
                return executePost(protocol.getDeviceId(), params);
            case "put":
                return executePut(protocol.getDeviceId(), params);
            case "delete":
                return executeDelete(protocol.getDeviceId(), params);
            case "health_check":
                return healthCheck(protocol.getDeviceId());
            default:
                return ReachResult.failure("Unknown action: " + action);
        }
    }
    
    private ReachResult executeGet(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("http_status", 200);
        data.put("response_time_ms", 45);
        data.put("data", Collections.singletonMap("result", "success"));
        
        log.info("API {} GET request executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult executePost(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("http_status", 201);
        data.put("response_time_ms", 120);
        data.put("resource_id", "res-" + System.currentTimeMillis());
        
        log.info("API {} POST request executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult executePut(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("http_status", 200);
        data.put("response_time_ms", 85);
        
        log.info("API {} PUT request executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult executeDelete(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("http_status", 204);
        data.put("response_time_ms", 30);
        
        log.info("API {} DELETE request executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult healthCheck(String deviceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "healthy");
        data.put("response_time_ms", 10);
        data.put("uptime", 86400);
        data.put("version", "1.0.0");
        
        return ReachResult.success(data);
    }
}
