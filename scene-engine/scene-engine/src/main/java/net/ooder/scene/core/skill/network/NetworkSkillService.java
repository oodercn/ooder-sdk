package net.ooder.scene.core.skill.network;

import net.ooder.scene.core.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络服务技能
 *
 * <p>包装SDK网络能力，添加安全检查和审计日志</p>
 */
public class NetworkSkillService extends SecureSkillService {

    private NetworkSdkWrapper networkSdkWrapper;

    @Override
    protected Object doExecute(SkillRequest request) {
        String operation = request.getOperation();
        switch (operation) {
            case "get":
                return httpGet(request);
            case "post":
                return httpPost(request);
            case "put":
                return httpPut(request);
            case "delete":
                return httpDelete(request);
            case "head":
                return httpHead(request);
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + operation);
        }
    }

    @Override
    protected String getResourceType() {
        return "network";
    }

    private Object httpGet(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String url = (String) params.get("url");
        Map<String, String> headers = params.containsKey("headers") ? (Map<String, String>) params.get("headers") : null;
        Map<String, Object> options = params.containsKey("options") ? (Map<String, Object>) params.get("options") : null;
        
        NetworkResponse response = networkSdkWrapper.get(url, headers, options);
        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("statusCode", response.getStatusCode());
        result.put("headers", response.getHeaders());
        result.put("body", response.getBody());
        result.put("duration", response.getDuration());
        return result;
    }

    private Object httpPost(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String url = (String) params.get("url");
        Object body = params.get("body");
        Map<String, String> headers = params.containsKey("headers") ? (Map<String, String>) params.get("headers") : null;
        Map<String, Object> options = params.containsKey("options") ? (Map<String, Object>) params.get("options") : null;
        
        NetworkResponse response = networkSdkWrapper.post(url, body, headers, options);
        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("statusCode", response.getStatusCode());
        result.put("headers", response.getHeaders());
        result.put("body", response.getBody());
        result.put("duration", response.getDuration());
        return result;
    }

    private Object httpPut(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String url = (String) params.get("url");
        Object body = params.get("body");
        Map<String, String> headers = params.containsKey("headers") ? (Map<String, String>) params.get("headers") : null;
        Map<String, Object> options = params.containsKey("options") ? (Map<String, Object>) params.get("options") : null;
        
        NetworkResponse response = networkSdkWrapper.put(url, body, headers, options);
        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("statusCode", response.getStatusCode());
        result.put("headers", response.getHeaders());
        result.put("body", response.getBody());
        result.put("duration", response.getDuration());
        return result;
    }

    private Object httpDelete(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String url = (String) params.get("url");
        Map<String, String> headers = params.containsKey("headers") ? (Map<String, String>) params.get("headers") : null;
        Map<String, Object> options = params.containsKey("options") ? (Map<String, Object>) params.get("options") : null;
        
        NetworkResponse response = networkSdkWrapper.delete(url, headers, options);
        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("statusCode", response.getStatusCode());
        result.put("headers", response.getHeaders());
        result.put("body", response.getBody());
        result.put("duration", response.getDuration());
        return result;
    }

    private Object httpHead(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String url = (String) params.get("url");
        Map<String, String> headers = params.containsKey("headers") ? (Map<String, String>) params.get("headers") : null;
        Map<String, Object> options = params.containsKey("options") ? (Map<String, Object>) params.get("options") : null;
        
        NetworkResponse response = networkSdkWrapper.head(url, headers, options);
        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("statusCode", response.getStatusCode());
        result.put("headers", response.getHeaders());
        result.put("duration", response.getDuration());
        return result;
    }

    @Override
    protected String getSkillId() {
        return "skill-network";
    }
}

/**
 * 网络SDK包装器
 *
 * <p>包装底层SDK网络能力，提供统一接口</p>
 */
class NetworkSdkWrapper {

    public NetworkResponse get(String url, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        return new NetworkResponse(200, responseHeaders, "{\"message\": \"Success\"}", 100);
    }

    public NetworkResponse post(String url, Object body, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        return new NetworkResponse(201, responseHeaders, "{\"message\": \"Created\"}", 150);
    }

    public NetworkResponse put(String url, Object body, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        return new NetworkResponse(200, responseHeaders, "{\"message\": \"Updated\"}", 120);
    }

    public NetworkResponse delete(String url, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<>();
        return new NetworkResponse(204, responseHeaders, null, 80);
    }

    public NetworkResponse head(String url, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        responseHeaders.put("Content-Length", "20");
        return new NetworkResponse(200, responseHeaders, null, 50);
    }
}

/**
 * 网络响应
 */
class NetworkResponse {
    private int statusCode;
    private Map<String, String> headers;
    private Object body;
    private long duration;
    
    public NetworkResponse(int statusCode, Map<String, String> headers, Object body, long duration) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
        this.duration = duration;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public Object getBody() {
        return body;
    }
    
    public long getDuration() {
        return duration;
    }
}
