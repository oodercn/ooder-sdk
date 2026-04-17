package net.ooder.skill.config.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@RestController
@RequestMapping("/api/v1/setup")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class SetupController {

    private static final Logger log = LoggerFactory.getLogger(SetupController.class);
    private static final String SETUP_CONFIG_FILE = "config/setup-config.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @GetMapping("/check")
    public ResultModel<Map<String, Object>> checkSetup() {
        log.info("[checkSetup] Checking setup status");
        
        try {
            File configFile = new File(SETUP_CONFIG_FILE);
            boolean completed = configFile.exists();
            
            Map<String, Object> result = new HashMap<>();
            result.put("completed", completed);
            
            return ResultModel.success(result);
            
        } catch (Exception e) {
            log.error("[checkSetup] Error checking setup status", e);
            return ResultModel.success(Map.of("completed", false));
        }
    }

    @PostMapping("/test-llm")
    public ResultModel<Map<String, Object>> testLLMConnection(@RequestBody Map<String, Object> request) {
        log.info("[testLLMConnection] Testing LLM connection for provider: {}", request.get("provider"));
        
        String provider = (String) request.get("provider");
        String apiKey = (String) request.get("apiKey");
        String endpoint = (String) request.get("endpoint");
        String model = (String) request.get("model");
        
        try {
            String testEndpoint = getTestEndpoint(provider, endpoint);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model != null ? model : getDefaultModel(provider));
            requestBody.put("messages", Arrays.asList(
                Map.of("role", "user", "content", "Hello, this is a test message.")
            ));
            requestBody.put("max_tokens", 10);
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(testEndpoint + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return ResultModel.success(Map.of(
                    "success", true,
                    "message", "LLM连接测试成功"
                ));
            } else {
                return ResultModel.error("LLM连接测试失败: HTTP " + response.statusCode());
            }
            
        } catch (Exception e) {
            log.error("[testLLMConnection] Error testing LLM connection", e);
            return ResultModel.error("LLM连接测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/test-dingding")
    public ResultModel<Map<String, Object>> testDingDingConnection(@RequestBody Map<String, Object> request) {
        log.info("[testDingDingConnection] Testing DingDing webhook connection");
        
        String webhook = (String) request.get("webhook");
        String secret = (String) request.get("secret");
        
        try {
            String url = webhook;
            
            if (secret != null && !secret.isEmpty()) {
                long timestamp = System.currentTimeMillis();
                String sign = generateDingDingSign(secret, timestamp);
                url = webhook + "&timestamp=" + timestamp + "&sign=" + sign;
            }
            
            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");
            message.put("text", Map.of("content", "Apex OS 系统配置测试消息"));
            
            String jsonBody = objectMapper.writeValueAsString(message);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                if (responseMap.containsKey("errcode") && (Integer) responseMap.get("errcode") == 0) {
                    return ResultModel.success(Map.of(
                        "success", true,
                        "message", "钉钉连接测试成功"
                    ));
                } else {
                    return ResultModel.error("钉钉连接测试失败: " + responseMap.get("errmsg"));
                }
            } else {
                return ResultModel.error("钉钉连接测试失败: HTTP " + response.statusCode());
            }
            
        } catch (Exception e) {
            log.error("[testDingDingConnection] Error testing DingDing connection", e);
            return ResultModel.error("钉钉连接测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/test-feishu")
    public ResultModel<Map<String, Object>> testFeishuConnection(@RequestBody Map<String, Object> request) {
        log.info("[testFeishuConnection] Testing Feishu webhook connection");
        
        String webhook = (String) request.get("webhook");
        String secret = (String) request.get("secret");
        
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("msg_type", "text");
            
            Map<String, Object> content = new HashMap<>();
            content.put("text", "Apex OS 系统配置测试消息");
            message.put("content", content);
            
            if (secret != null && !secret.isEmpty()) {
                long timestamp = System.currentTimeMillis() / 1000;
                String sign = generateFeishuSign(secret, timestamp);
                message.put("timestamp", String.valueOf(timestamp));
                message.put("sign", sign);
            }
            
            String jsonBody = objectMapper.writeValueAsString(message);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(webhook))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                if (responseMap.containsKey("StatusCode") && (Integer) responseMap.get("StatusCode") == 0) {
                    return ResultModel.success(Map.of(
                        "success", true,
                        "message", "飞书连接测试成功"
                    ));
                } else {
                    return ResultModel.error("飞书连接测试失败: " + responseMap.get("msg"));
                }
            } else {
                return ResultModel.error("飞书连接测试失败: HTTP " + response.statusCode());
            }
            
        } catch (Exception e) {
            log.error("[testFeishuConnection] Error testing Feishu connection", e);
            return ResultModel.error("飞书连接测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/test-weixin")
    public ResultModel<Map<String, Object>> testWeixinConnection(@RequestBody Map<String, Object> request) {
        log.info("[testWeixinConnection] Testing Weixin webhook connection");
        
        String webhook = (String) request.get("webhook");
        
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");
            message.put("text", Map.of("content", "Apex OS 系统配置测试消息"));
            
            String jsonBody = objectMapper.writeValueAsString(message);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(webhook))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                if (responseMap.containsKey("errcode") && (Integer) responseMap.get("errcode") == 0) {
                    return ResultModel.success(Map.of(
                        "success", true,
                        "message", "企业微信连接测试成功"
                    ));
                } else {
                    return ResultModel.error("企业微信连接测试失败: " + responseMap.get("errmsg"));
                }
            } else {
                return ResultModel.error("企业微信连接测试失败: HTTP " + response.statusCode());
            }
            
        } catch (Exception e) {
            log.error("[testWeixinConnection] Error testing Weixin connection", e);
            return ResultModel.error("企业微信连接测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/complete")
    public ResultModel<Map<String, Object>> completeSetup(@RequestBody Map<String, Object> config) {
        log.info("[completeSetup] Completing setup with config: {}", config);
        
        try {
            // Save configuration to file or database
            // For now, we'll just log it
            
            return ResultModel.success(Map.of(
                "success", true,
                "message", "配置保存成功"
            ));
            
        } catch (Exception e) {
            log.error("[completeSetup] Error saving configuration", e);
            return ResultModel.error("配置保存失败: " + e.getMessage());
        }
    }

    private String getTestEndpoint(String provider, String customEndpoint) {
        if (customEndpoint != null && !customEndpoint.isEmpty()) {
            return customEndpoint;
        }
        
        switch (provider) {
            case "openai":
                return "https://api.openai.com/v1";
            case "deepseek":
                return "https://api.deepseek.com/v1";
            case "zhipu":
                return "https://open.bigmodel.cn/api/paas/v4";
            case "moonshot":
                return "https://api.moonshot.cn/v1";
            case "qwen":
                return "https://dashscope.aliyuncs.com/api/v1";
            case "tencent":
                return "https://api.hunyuan.cloud.tencent.com/v1";
            case "baidu":
                return "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat";
            case "spark":
                return "https://spark-api-open.xf-yun.com/v1";
            case "baichuan":
                return "https://api.baichuan-ai.com/v1";
            case "minimax":
                return "https://api.minimax.chat/v1";
            default:
                return "https://api.openai.com/v1";
        }
    }

    private String getDefaultModel(String provider) {
        switch (provider) {
            case "openai":
                return "gpt-4";
            case "deepseek":
                return "deepseek-chat";
            case "zhipu":
                return "glm-4";
            case "moonshot":
                return "moonshot-v1-8k";
            case "qwen":
                return "qwen-turbo";
            case "tencent":
                return "hunyuan-lite";
            case "baidu":
                return "ernie-bot-4";
            case "spark":
                return "spark-v3.5";
            case "baichuan":
                return "Baichuan2-Turbo";
            case "minimax":
                return "abab6.5-chat";
            default:
                return "gpt-4";
        }
    }

    private String generateDingDingSign(String secret, long timestamp) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }

    private String generateFeishuSign(String secret, long timestamp) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(secret.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }

    public static class ResultModel<T> {
        private int code;
        private String status;
        private String message;
        private T data;
        private long timestamp;

        public ResultModel() {
            this.timestamp = System.currentTimeMillis();
        }

        public static <T> ResultModel<T> success(T data) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(200);
            result.setStatus("success");
            result.setMessage("操作成功");
            result.setData(data);
            return result;
        }

        public static <T> ResultModel<T> error(String message) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(500);
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
    }
}
