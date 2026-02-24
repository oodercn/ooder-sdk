package net.ooder.sdk.reach.impl;

import net.ooder.sdk.reach.ReachExecutor;
import net.ooder.sdk.reach.ReachProtocol;
import net.ooder.sdk.reach.ReachResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FirewallReachExecutor implements ReachExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(FirewallReachExecutor.class);
    
    @Override
    public boolean supports(String deviceType) {
        return "firewall".equalsIgnoreCase(deviceType);
    }
    
    @Override
    public ReachResult execute(ReachProtocol protocol) {
        String action = protocol.getAction();
        Map<String, Object> params = protocol.getParams();
        
        log.info("Executing firewall action: {} on device: {}", action, protocol.getDeviceId());
        
        switch (action.toLowerCase()) {
            case "configure":
                return configureFirewall(protocol.getDeviceId(), params);
            case "add_rule":
                return addRule(protocol.getDeviceId(), params);
            case "remove_rule":
                return removeRule(protocol.getDeviceId(), params);
            case "list_rules":
                return listRules(protocol.getDeviceId());
            case "enable":
            case "disable":
                return toggleFirewall(protocol.getDeviceId(), action);
            default:
                return ReachResult.failure("Unknown action: " + action);
        }
    }
    
    private ReachResult configureFirewall(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "configured");
        data.put("mode", params != null ? params.getOrDefault("mode", "allow") : "allow");
        
        log.info("Firewall {} configured", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult addRule(String deviceId, Map<String, Object> params) {
        String ruleId = "rule-" + System.currentTimeMillis();
        
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("ruleId", ruleId);
        data.put("status", "added");
        data.put("rule", params);
        
        log.info("Firewall rule {} added to {}", ruleId, deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult removeRule(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "removed");
        data.put("ruleId", params != null ? params.get("ruleId") : null);
        
        log.info("Firewall rule removed from {}", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult listRules(String deviceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("rules", Arrays.asList(
            createRule("rule-001", "ALLOW", "192.168.1.0/24", "ANY"),
            createRule("rule-002", "DENY", "10.0.0.0/8", "ANY"),
            createRule("rule-003", "ALLOW", "ANY", "TCP:80,443"),
            createRule("rule-004", "DENY", "ANY", "TCP:23")
        ));
        
        return ReachResult.success(data);
    }
    
    private ReachResult toggleFirewall(String deviceId, String action) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "enable".equalsIgnoreCase(action) ? "enabled" : "disabled");
        
        log.info("Firewall {} {}", deviceId, action);
        return ReachResult.success(data);
    }
    
    private Map<String, Object> createRule(String id, String type, String source, String destination) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("id", id);
        rule.put("type", type);
        rule.put("source", source);
        rule.put("destination", destination);
        rule.put("enabled", true);
        return rule;
    }
}
