package net.ooder.sdk.reach.impl;

import net.ooder.sdk.reach.ReachExecutor;
import net.ooder.sdk.reach.ReachProtocol;
import net.ooder.sdk.reach.ReachResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DatabaseReachExecutor implements ReachExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(DatabaseReachExecutor.class);
    
    @Override
    public boolean supports(String deviceType) {
        return "database".equalsIgnoreCase(deviceType) || 
               "db".equalsIgnoreCase(deviceType) ||
               "mysql".equalsIgnoreCase(deviceType) ||
               "postgresql".equalsIgnoreCase(deviceType);
    }
    
    @Override
    public ReachResult execute(ReachProtocol protocol) {
        String action = protocol.getAction();
        Map<String, Object> params = protocol.getParams();
        
        log.info("Executing database action: {} on device: {}", action, protocol.getDeviceId());
        
        switch (action.toLowerCase()) {
            case "query":
                return executeQuery(protocol.getDeviceId(), params);
            case "update":
                return executeUpdate(protocol.getDeviceId(), params);
            case "insert":
                return executeInsert(protocol.getDeviceId(), params);
            case "delete":
                return executeDelete(protocol.getDeviceId(), params);
            case "create_table":
                return createTable(protocol.getDeviceId(), params);
            case "backup":
                return backup(protocol.getDeviceId(), params);
            default:
                return ReachResult.failure("Unknown action: " + action);
        }
    }
    
    private ReachResult executeQuery(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("rows_affected", 0);
        data.put("rows_returned", 10);
        data.put("execution_time_ms", 15);
        data.put("results", Arrays.asList(
            createRow(1, "user1", "user1@example.com"),
            createRow(2, "user2", "user2@example.com")
        ));
        
        log.info("Database {} query executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult executeUpdate(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("rows_affected", 1);
        data.put("execution_time_ms", 5);
        
        log.info("Database {} update executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult executeInsert(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("rows_affected", 1);
        data.put("last_insert_id", System.currentTimeMillis());
        data.put("execution_time_ms", 3);
        
        log.info("Database {} insert executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult executeDelete(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("rows_affected", 1);
        data.put("execution_time_ms", 2);
        
        log.info("Database {} delete executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult createTable(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("table_name", params != null ? params.get("tableName") : "unknown");
        data.put("execution_time_ms", 50);
        
        log.info("Database {} table created", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult backup(String deviceId, Map<String, Object> params) {
        String backupId = "backup-" + System.currentTimeMillis();
        
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("backupId", backupId);
        data.put("size", 1024000);
        
        log.info("Database {} backup created: {}", deviceId, backupId);
        return ReachResult.success(data);
    }
    
    private Map<String, Object> createRow(int id, String name, String email) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", id);
        row.put("name", name);
        row.put("email", email);
        return row;
    }
}
