package net.ooder.skill.capability.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/capabilities")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CapabilityVersionController {

    private static final Logger log = LoggerFactory.getLogger(CapabilityVersionController.class);

    @GetMapping("/{capabilityId}/versions")
    public ResultModel<List<Map<String, Object>>> getVersions(@PathVariable String capabilityId) {
        log.info("[getVersions] Getting versions for capability: {}", capabilityId);
        
        List<Map<String, Object>> versions = new ArrayList<>();
        
        Map<String, Object> v1 = new HashMap<>();
        v1.put("version", "1.0.0");
        v1.put("status", "current");
        v1.put("releaseDate", new Date());
        v1.put("author", "system");
        v1.put("changes", Arrays.asList(
            createChange("add", "初始版本发布", "核心功能实现")
        ));
        v1.put("config", createDefaultConfig());
        versions.add(v1);
        
        return ResultModel.success(versions);
    }

    @GetMapping("/{capabilityId}/versions/{version}")
    public ResultModel<Map<String, Object>> getVersion(
            @PathVariable String capabilityId,
            @PathVariable String version) {
        
        log.info("[getVersion] Getting version {} for capability: {}", version, capabilityId);
        
        Map<String, Object> versionData = new HashMap<>();
        versionData.put("version", version);
        versionData.put("status", "current");
        versionData.put("releaseDate", new Date());
        versionData.put("author", "system");
        versionData.put("changes", Arrays.asList(
            createChange("add", "版本详情", "版本详细信息")
        ));
        versionData.put("config", createDefaultConfig());
        
        return ResultModel.success(versionData);
    }

    @PostMapping("/{capabilityId}/versions")
    public ResultModel<Map<String, Object>> createVersion(
            @PathVariable String capabilityId,
            @RequestBody Map<String, Object> request) {
        
        log.info("[createVersion] Creating new version for capability: {}", capabilityId);
        
        Map<String, Object> newVersion = new HashMap<>();
        newVersion.put("version", request.getOrDefault("version", "1.0.1"));
        newVersion.put("status", "latest");
        newVersion.put("releaseDate", new Date());
        newVersion.put("author", "current-user");
        newVersion.put("changes", Arrays.asList(
            createChange("modify", "版本更新", request.getOrDefault("changelog", "").toString())
        ));
        newVersion.put("config", request.getOrDefault("config", createDefaultConfig()));
        
        return ResultModel.success(newVersion);
    }

    @PostMapping("/{capabilityId}/versions/{version}/rollback")
    public ResultModel<Map<String, Object>> rollbackVersion(
            @PathVariable String capabilityId,
            @PathVariable String version) {
        
        log.info("[rollbackVersion] Rolling back to version {} for capability: {}", version, capabilityId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("capabilityId", capabilityId);
        result.put("rolledBackTo", version);
        result.put("status", "success");
        result.put("message", "已成功回滚到版本 " + version);
        
        return ResultModel.success(result);
    }

    private Map<String, Object> createChange(String type, String title, String desc) {
        Map<String, Object> change = new HashMap<>();
        change.put("type", type);
        change.put("title", title);
        change.put("desc", desc);
        return change;
    }

    private Map<String, Object> createDefaultConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("timeout", 30000);
        config.put("retries", 3);
        return config;
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
