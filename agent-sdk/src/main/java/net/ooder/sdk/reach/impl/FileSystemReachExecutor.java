package net.ooder.sdk.reach.impl;

import net.ooder.sdk.reach.ReachExecutor;
import net.ooder.sdk.reach.ReachProtocol;
import net.ooder.sdk.reach.ReachResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FileSystemReachExecutor implements ReachExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(FileSystemReachExecutor.class);
    
    @Override
    public boolean supports(String deviceType) {
        return "filesystem".equalsIgnoreCase(deviceType) || 
               "fs".equalsIgnoreCase(deviceType) ||
               "storage".equalsIgnoreCase(deviceType);
    }
    
    @Override
    public ReachResult execute(ReachProtocol protocol) {
        String action = protocol.getAction();
        Map<String, Object> params = protocol.getParams();
        
        log.info("Executing filesystem action: {} on device: {}", action, protocol.getDeviceId());
        
        switch (action.toLowerCase()) {
            case "read":
                return readFile(protocol.getDeviceId(), params);
            case "write":
                return writeFile(protocol.getDeviceId(), params);
            case "delete":
                return deleteFile(protocol.getDeviceId(), params);
            case "list":
                return listFiles(protocol.getDeviceId(), params);
            case "mkdir":
                return createDirectory(protocol.getDeviceId(), params);
            case "stat":
                return getStat(protocol.getDeviceId(), params);
            default:
                return ReachResult.failure("Unknown action: " + action);
        }
    }
    
    private ReachResult readFile(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("path", params != null ? params.get("path") : "/");
        data.put("size", 1024);
        data.put("content", "file content placeholder");
        
        log.info("Filesystem {} read executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult writeFile(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("path", params != null ? params.get("path") : "/file.txt");
        data.put("bytes_written", params != null ? params.get("content") : "".toString().length());
        
        log.info("Filesystem {} write executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult deleteFile(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("path", params != null ? params.get("path") : "/file.txt");
        data.put("deleted", true);
        
        log.info("Filesystem {} delete executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult listFiles(String deviceId, Map<String, Object> params) {
        String path = params != null ? (String) params.get("path") : "/";
        
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("path", path);
        data.put("files", Arrays.asList(
            createFileInfo("file1.txt", 1024, "file"),
            createFileInfo("file2.txt", 2048, "file"),
            createFileInfo("dir1", 0, "directory")
        ));
        
        log.info("Filesystem {} list executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult createDirectory(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("path", params != null ? params.get("path") : "/new_dir");
        data.put("created", true);
        
        log.info("Filesystem {} mkdir executed", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult getStat(String deviceId, Map<String, Object> params) {
        String path = params != null ? (String) params.get("path") : "/";
        
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "success");
        data.put("path", path);
        data.put("stat", createFileInfo(path, 1024, "file"));
        
        return ReachResult.success(data);
    }
    
    private Map<String, Object> createFileInfo(String name, long size, String type) {
        Map<String, Object> info = new HashMap<>();
        info.put("name", name);
        info.put("size", size);
        info.put("type", type);
        info.put("modified", System.currentTimeMillis());
        info.put("permissions", "rw-r--r--");
        return info;
    }
}
