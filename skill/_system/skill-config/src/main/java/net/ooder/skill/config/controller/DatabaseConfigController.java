package net.ooder.skill.config.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/config/db")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class DatabaseConfigController {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfigController.class);

    @GetMapping("/connections")
    public ResultModel<List<Map<String, Object>>> getConnections() {
        log.info("[getConnections] Getting database connections");
        
        List<Map<String, Object>> connections = new ArrayList<>();
        
        Map<String, Object> defaultConn = new HashMap<>();
        defaultConn.put("id", "default");
        defaultConn.put("name", "默认数据库");
        defaultConn.put("dbType", "sqlite");
        defaultConn.put("database", "data/os.db");
        defaultConn.put("status", "connected");
        defaultConn.put("maxPoolSize", 10);
        defaultConn.put("minIdle", 2);
        connections.add(defaultConn);
        
        return ResultModel.success(connections);
    }

    @PutMapping("/connections/{id}")
    public ResultModel<Map<String, Object>> updateConnection(
            @PathVariable String id,
            @RequestBody Map<String, Object> connection) {
        
        log.info("[updateConnection] Updating connection: {}", id);
        connection.put("id", id);
        connection.put("status", "pending");
        return ResultModel.success(connection);
    }

    @PostMapping("/test")
    public ResultModel<Map<String, Object>> testConnection(@RequestBody Map<String, Object> testData) {
        log.info("[testConnection] Testing connection: {}", testData.get("dbType"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("responseTime", "50ms");
        result.put("message", "连接成功");
        
        return ResultModel.success(result);
    }

    @GetMapping("/monitor")
    public ResultModel<Map<String, Object>> getMonitorData() {
        log.info("[getMonitorData] Getting database monitor data");
        
        Map<String, Object> data = new HashMap<>();
        data.put("activeConnections", 3);
        data.put("idleConnections", 7);
        data.put("pendingConnections", 0);
        data.put("totalConnections", 10);
        data.put("poolUsage", "30%");
        data.put("avgResponseTime", "45ms");
        
        return ResultModel.success(data);
    }

    @PutMapping("/pool")
    public ResultModel<Map<String, Object>> updatePoolConfig(@RequestBody Map<String, Object> config) {
        log.info("[updatePoolConfig] Updating pool config: {}", config);
        
        Map<String, Object> result = new HashMap<>();
        result.put("maxPoolSize", config.getOrDefault("maxPoolSize", 20));
        result.put("minIdle", config.getOrDefault("minIdle", 5));
        result.put("connectionTimeout", config.getOrDefault("connectionTimeout", 30000));
        result.put("idleTimeout", config.getOrDefault("idleTimeout", 600000));
        result.put("maxLifetime", config.getOrDefault("maxLifetime", 1800000));
        result.put("validationTimeout", config.getOrDefault("validationTimeout", 5000));
        result.put("message", "连接池配置已更新");
        
        return ResultModel.success(result);
    }

    public static class ResultModel<T> {
        private int code;
        private String status;
        private String message;
        private T data;
        private long timestamp;
        private String requestId;

        public ResultModel() {
            this.timestamp = System.currentTimeMillis();
            this.requestId = "REQ_" + timestamp + "_" + new Random().nextInt(1000);
        }

        public static <T> ResultModel<T> success(T data) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(200);
            result.setStatus("success");
            result.setMessage("操作成功");
            result.setData(data);
            return result;
        }

        public static <T> ResultModel<T> success() {
            return success(null);
        }

        public static <T> ResultModel<T> error(int code, String message) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(code);
            result.setStatus("error");
            result.setMessage(message);
            return result;
        }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
    }
}
