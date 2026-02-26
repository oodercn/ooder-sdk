package net.ooder.sdk.reach.impl;

import net.ooder.sdk.reach.ReachExecutor;
import net.ooder.sdk.reach.ReachProtocol;
import net.ooder.sdk.reach.ReachResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CameraReachExecutor implements ReachExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(CameraReachExecutor.class);
    
    @Override
    public boolean supports(String deviceType) {
        return "camera".equalsIgnoreCase(deviceType) || "ipc".equalsIgnoreCase(deviceType);
    }
    
    @Override
    public ReachResult execute(ReachProtocol protocol) {
        String action = protocol.getAction();
        Map<String, Object> params = protocol.getParams();
        
        log.info("Executing camera action: {} on device: {}", action, protocol.getDeviceId());
        
        switch (action.toLowerCase()) {
            case "capture":
                return capture(protocol.getDeviceId(), params);
            case "start_record":
                return startRecord(protocol.getDeviceId(), params);
            case "stop_record":
                return stopRecord(protocol.getDeviceId());
            case "ptz":
                return ptzControl(protocol.getDeviceId(), params);
            case "get_stream":
                return getStream(protocol.getDeviceId());
            case "status":
                return getStatus(protocol.getDeviceId());
            default:
                return ReachResult.failure("Unknown action: " + action);
        }
    }
    
    private ReachResult capture(String deviceId, Map<String, Object> params) {
        String imageId = "img-" + System.currentTimeMillis();
        
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("imageId", imageId);
        data.put("format", params != null ? params.getOrDefault("format", "jpg") : "jpg");
        data.put("resolution", params != null ? params.getOrDefault("resolution", "1920x1080") : "1920x1080");
        data.put("timestamp", System.currentTimeMillis());
        data.put("size", 102400);
        
        log.info("Camera {} captured image {}", deviceId, imageId);
        return ReachResult.success(data);
    }
    
    private ReachResult startRecord(String deviceId, Map<String, Object> params) {
        String recordId = "rec-" + System.currentTimeMillis();
        
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("recordId", recordId);
        data.put("status", "recording");
        data.put("format", params != null ? params.getOrDefault("format", "mp4") : "mp4");
        data.put("duration", params != null ? params.getOrDefault("duration", 60) : 60);
        
        log.info("Camera {} started recording {}", deviceId, recordId);
        return ReachResult.success(data);
    }
    
    private ReachResult stopRecord(String deviceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "stopped");
        data.put("file_size", 52428800);
        
        log.info("Camera {} stopped recording", deviceId);
        return ReachResult.success(data);
    }
    
    private ReachResult ptzControl(String deviceId, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "ptz_moved");
        data.put("pan", params != null ? params.getOrDefault("pan", 0) : 0);
        data.put("tilt", params != null ? params.getOrDefault("tilt", 0) : 0);
        data.put("zoom", params != null ? params.getOrDefault("zoom", 1.0) : 1.0);
        
        log.info("Camera {} PTZ control: pan={}, tilt={}, zoom={}", 
            deviceId, data.get("pan"), data.get("tilt"), data.get("zoom"));
        return ReachResult.success(data);
    }
    
    private ReachResult getStream(String deviceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("stream_url", "rtsp://camera-" + deviceId + ":554/stream");
        data.put("protocol", "rtsp");
        data.put("codec", "H264");
        data.put("resolution", "1920x1080");
        data.put("framerate", 30);
        
        return ReachResult.success(data);
    }
    
    private ReachResult getStatus(String deviceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("status", "online");
        data.put("recording", false);
        data.put("storage_available", 1024 * 1024 * 1024);
        data.put("uptime", 86400);
        
        return ReachResult.success(data);
    }
}
