package net.ooder.skill.capability.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/capabilities")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CapabilityLogController {

    private static final Logger log = LoggerFactory.getLogger(CapabilityLogController.class);

    @GetMapping("/{capabilityId}/logs")
    public ResultModel<Map<String, Object>> getLogs(
            @PathVariable String capabilityId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String keyword) {
        
        log.info("[getLogs] Getting logs for capability: {}, page: {}, size: {}", capabilityId, page, size);
        
        List<Map<String, Object>> logs = new ArrayList<>();
        
        for (int i = 0; i < Math.min(size, 20); i++) {
            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("id", "req-" + (1000 + i));
            logEntry.put("timestamp", new Date(System.currentTimeMillis() - i * 60000));
            logEntry.put("level", i % 10 == 0 ? "ERROR" : "INFO");
            logEntry.put("status", i % 10 == 0 ? 500 : 200);
            logEntry.put("duration", 50 + (int)(Math.random() * 200));
            logEntry.put("action", new String[]{"invoke", "query", "update", "delete"}[i % 4]);
            logEntry.put("source", new String[]{"web", "api", "schedule", "event"}[i % 4]);
            logEntry.put("message", i % 10 == 0 ? "调用失败: 超时" : "调用成功");
            logEntry.put("request", createRequest(capabilityId));
            logEntry.put("response", createResponse(i % 10 != 0));
            logs.add(logEntry);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("logs", logs);
        result.put("total", 100);
        result.put("page", page);
        result.put("size", size);
        
        return ResultModel.success(result);
    }

    @GetMapping("/{capabilityId}/logs/stats")
    public ResultModel<Map<String, Object>> getLogStats(@PathVariable String capabilityId) {
        log.info("[getLogStats] Getting log stats for capability: {}", capabilityId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 100);
        stats.put("success", 90);
        stats.put("error", 10);
        stats.put("avgDuration", "125ms");
        stats.put("successRate", "90%");
        
        return ResultModel.success(stats);
    }

    @GetMapping("/{capabilityId}/logs/export")
    public ResultModel<String> exportLogs(@PathVariable String capabilityId) {
        log.info("[exportLogs] Exporting logs for capability: {}", capabilityId);
        
        String downloadUrl = "/api/v1/capabilities/" + capabilityId + "/logs/download";
        
        return ResultModel.success(downloadUrl);
    }

    private Map<String, Object> createRequest(String capabilityId) {
        Map<String, Object> request = new HashMap<>();
        request.put("method", "POST");
        request.put("path", "/api/v1/capabilities/" + capabilityId + "/invoke");
        return request;
    }

    private Map<String, Object> createResponse(boolean success) {
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("data", Collections.singletonMap("result", "success"));
        } else {
            response.put("code", 500);
            response.put("error", "Internal Error");
        }
        return response;
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
