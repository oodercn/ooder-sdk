package net.ooder.sdk.core.capability.impl;

import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.CapabilityStatus;
import net.ooder.sdk.api.scene.CapabilityInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CapabilityInvokerImpl implements CapabilityInvoker {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityInvokerImpl.class);
    
    private CapRegistry capRegistry;
    private final Map<String, InvokeStats> invokeStatsMap;
    
    public CapabilityInvokerImpl() {
        this.invokeStatsMap = new ConcurrentHashMap<>();
    }
    
    public CapabilityInvokerImpl(CapRegistry capRegistry) {
        this.capRegistry = capRegistry;
        this.invokeStatsMap = new ConcurrentHashMap<>();
    }
    
    public void setCapRegistry(CapRegistry capRegistry) {
        this.capRegistry = capRegistry;
    }
    
    @Override
    public CompletableFuture<Object> invoke(String sceneId, String capId, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                log.debug("Invoking capability [{}] in scene [{}] with params: {}", capId, sceneId, params);
                
                // 检查capRegistry是否已设置
                if (capRegistry == null) {
                    throw new RuntimeException("CapRegistry not initialized");
                }
                
                // 查找能力
                Capability capability = capRegistry.findById(capId);
                if (capability == null) {
                    throw new RuntimeException("Capability not found: " + capId);
                }
                
                // 检查能力状态
                if (capability.getStatus() != CapabilityStatus.ACTIVE) {
                    throw new RuntimeException("Capability is not active: " + capId + ", status: " + capability.getStatus());
                }
                
                // 执行调用（这里简化处理，实际应该通过代理调用）
                Object result = doInvoke(capability, params);
                
                // 记录调用统计
                recordInvokeSuccess(capId, System.currentTimeMillis() - startTime);
                
                log.debug("Capability [{}] invoked successfully, result: {}", capId, result);
                return result;
                
            } catch (Exception e) {
                log.error("Failed to invoke capability [{}]: {}", capId, e.getMessage());
                recordInvokeFailure(capId);
                throw new RuntimeException("Capability invocation failed: " + capId, e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Object> invoke(String capId, Map<String, Object> params) {
        // 检查capRegistry是否已设置
        if (capRegistry == null) {
            CompletableFuture<Object> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("CapRegistry not initialized"));
            return future;
        }
        
        // 查找能力所属的场景
        Capability capability = capRegistry.findById(capId);
        if (capability == null) {
            CompletableFuture<Object> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Capability not found: " + capId));
            return future;
        }
        String sceneId = capability.getConfig() != null ? 
            (String) capability.getConfig().get("sceneId") : null;
        return invoke(sceneId, capId, params);
    }
    
    @Override
    public CompletableFuture<Object> invokeAsync(String sceneId, String capId, Map<String, Object> params) {
        // 异步调用与同步调用类似，但返回CompletableFuture
        return invoke(sceneId, capId, params);
    }
    
    @Override
    public CompletableFuture<Boolean> isAvailable(String sceneId, String capId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (capRegistry == null) {
                    return false;
                }
                
                Capability capability = capRegistry.findById(capId);
                if (capability == null) {
                    return false;
                }
                
                // 检查能力状态
                if (capability.getStatus() != CapabilityStatus.ACTIVE) {
                    return false;
                }
                
                // 检查能力配置中的可用性
                Map<String, Object> config = capability.getConfig();
                if (config != null && config.containsKey("available")) {
                    return Boolean.TRUE.equals(config.get("available"));
                }
                
                return true;
                
            } catch (Exception e) {
                log.warn("Error checking capability availability [{}]: {}", capId, e.getMessage());
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<CapabilityMetadata> getMetadata(String sceneId, String capId) {
        return CompletableFuture.supplyAsync(() -> {
            if (capRegistry == null) {
                throw new RuntimeException("CapRegistry not initialized");
            }
            
            Capability capability = capRegistry.findById(capId);
            if (capability == null) {
                throw new RuntimeException("Capability not found: " + capId);
            }
            
            CapabilityMetadata metadata = new CapabilityMetadata();
            metadata.setCapId(capId);
            metadata.setName(capability.getName());
            metadata.setDescription(capability.getDescription());
            metadata.setSceneId(sceneId);
            
            // 从配置中获取提供者信息
            Map<String, Object> config = capability.getConfig();
            if (config != null) {
                metadata.setProviderAgentId((String) config.get("providerAgentId"));
                metadata.setAsync(Boolean.TRUE.equals(config.get("async")));
            }
            
            // 获取调用统计
            InvokeStats stats = invokeStatsMap.get(capId);
            if (stats != null) {
                metadata.setAverageInvokeTime(stats.getAverageTime());
                metadata.setInvokeCount(stats.getCount());
            }
            
            return metadata;
        });
    }
    
    @Override
    public CompletableFuture<Object> invokeWithFallback(String sceneId, String capId, Map<String, Object> params, String fallbackCapId) {
        return invoke(sceneId, capId, params).exceptionally(ex -> {
            log.warn("Primary capability [{}] failed, trying fallback [{}]: {}", 
                capId, fallbackCapId, ex.getMessage());
            
            try {
                if (capRegistry == null) {
                    throw new RuntimeException("CapRegistry not initialized");
                }
                
                // 尝试调用fallback能力
                Capability fallbackCap = capRegistry.findById(fallbackCapId);
                if (fallbackCap == null) {
                    throw new RuntimeException("Fallback capability not found: " + fallbackCapId);
                }
                
                return doInvoke(fallbackCap, params);
                
            } catch (Exception fallbackEx) {
                log.error("Fallback capability [{}] also failed: {}", fallbackCapId, fallbackEx.getMessage());
                throw new RuntimeException("Both primary and fallback capabilities failed", fallbackEx);
            }
        });
    }
    
    /**
     * 实际执行能力调用
     */
    private Object doInvoke(Capability capability, Map<String, Object> params) {
        // 这里简化处理，实际应该通过RPC或HTTP调用能力提供者
        // 可以扩展为支持多种调用方式：HTTP、gRPC、本地方法调用等
        
        Map<String, Object> config = capability.getConfig();
        if (config == null) {
            throw new RuntimeException("Capability config is null: " + capability.getCapId());
        }
        
        String endpoint = (String) config.get("endpoint");
        if (endpoint == null) {
            // 如果没有端点，返回模拟结果
            return simulateInvoke(capability, params);
        }
        
        // 根据端点类型选择调用方式
        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            return invokeHttp(endpoint, params);
        } else if (endpoint.startsWith("grpc://")) {
            return invokeGrpc(endpoint, params);
        } else {
            return invokeLocal(capability, params);
        }
    }
    
    /**
     * HTTP调用
     * 
     * <p>通过HTTP POST请求调用远程能力</p>
     */
    private Object invokeHttp(String endpoint, Map<String, Object> params) {
        log.debug("HTTP invoke to: {}", endpoint);
        
        try {
            // 创建URL连接
            java.net.URL url = new java.net.URL(endpoint);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            
            // 设置请求方法
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            
            // 发送请求体
            String jsonBody = convertToJson(params);
            try (java.io.OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // 读取响应
            int responseCode = conn.getResponseCode();
            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                String response = readResponse(conn.getInputStream());
                return parseJson(response);
            } else {
                String errorResponse = readResponse(conn.getErrorStream());
                throw new RuntimeException("HTTP error " + responseCode + ": " + errorResponse);
            }
            
        } catch (Exception e) {
            log.error("HTTP invoke failed to endpoint [{}]: {}", endpoint, e.getMessage());
            throw new RuntimeException("HTTP invoke failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * gRPC调用
     * 
     * <p>通过gRPC协议调用远程能力</p>
     * <p>注：需要引入gRPC相关依赖</p>
     */
    private Object invokeGrpc(String endpoint, Map<String, Object> params) {
        log.debug("gRPC invoke to: {}", endpoint);
        
        try {
            // 解析endpoint，格式：grpc://host:port/service/method
            String grpcAddress = endpoint.substring(7); // 去掉 grpc:// 前缀
            String[] parts = grpcAddress.split("/");
            if (parts.length < 3) {
                throw new RuntimeException("Invalid gRPC endpoint format: " + endpoint);
            }
            
            String hostPort = parts[0];
            String serviceName = parts[1];
            String methodName = parts[2];
            
            // TODO: 使用gRPC客户端进行调用
            // 这里简化处理，返回模拟结果
            // 实际实现需要使用gRPC生成的stub类
            log.warn("gRPC invoke is simulated, actual implementation requires gRPC client setup");
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "simulated_grpc");
            result.put("endpoint", endpoint);
            result.put("service", serviceName);
            result.put("method", methodName);
            result.put("params", params);
            return result;
            
        } catch (Exception e) {
            log.error("gRPC invoke failed to endpoint [{}]: {}", endpoint, e.getMessage());
            throw new RuntimeException("gRPC invoke failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * 本地调用
     * 
     * <p>调用本地注册的Skill实现</p>
     */
    private Object invokeLocal(Capability capability, Map<String, Object> params) {
        log.debug("Local invoke for capability: {}", capability.getCapId());
        
        try {
            // 从能力配置中获取本地实现类名
            Map<String, Object> config = capability.getConfig();
            if (config == null) {
                throw new RuntimeException("Capability config is null");
            }
            
            String implClassName = (String) config.get("implClass");
            if (implClassName == null) {
                throw new RuntimeException("Implementation class not configured for capability: " + capability.getCapId());
            }
            
            // 获取或创建实现类实例
            Object instance = getOrCreateInstance(implClassName);
            
            // 获取要调用的方法名
            String methodName = (String) config.get("methodName");
            if (methodName == null) {
                methodName = "execute"; // 默认方法名
            }
            
            // 通过反射调用方法
            java.lang.reflect.Method method = findMethod(instance.getClass(), methodName, params);
            if (method == null) {
                throw new RuntimeException("Method not found: " + methodName + " in class " + implClassName);
            }
            
            // 调用方法
            Object result = method.invoke(instance, params);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("capability", capability.getCapId());
            response.put("result", result);
            return response;
            
        } catch (Exception e) {
            log.error("Local invoke failed for capability [{}]: {}", capability.getCapId(), e.getMessage());
            throw new RuntimeException("Local invoke failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取或创建实现类实例
     */
    private Object getOrCreateInstance(String className) throws Exception {
        // 简单的单例缓存
        return instanceCache.computeIfAbsent(className, k -> {
            try {
                Class<?> clazz = Class.forName(k);
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance of class: " + k, e);
            }
        });
    }
    
    private final Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    
    /**
     * 查找匹配的方法
     */
    private java.lang.reflect.Method findMethod(Class<?> clazz, String methodName, Map<String, Object> params) {
        // 简化实现：查找第一个匹配方法名的方法
        for (java.lang.reflect.Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
    
    /**
     * 将Map转换为JSON字符串
     */
    private String convertToJson(Map<String, Object> params) {
        // 简化实现，实际应该使用Jackson或Gson
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 读取响应流
     */
    private String readResponse(java.io.InputStream is) throws java.io.IOException {
        if (is == null) return "";
        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
    
    /**
     * 解析JSON字符串为Map
     */
    private Map<String, Object> parseJson(String json) {
        // 简化实现，实际应该使用Jackson或Gson
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", json);
        return result;
    }
    
    /**
     * 模拟调用（用于测试或没有实际端点的情况）
     */
    private Object simulateInvoke(Capability capability, Map<String, Object> params) {
        log.debug("Simulating invoke for capability: {}", capability.getCapId());
        Map<String, Object> result = new HashMap<>();
        result.put("status", "simulated");
        result.put("capabilityId", capability.getCapId());
        result.put("capabilityName", capability.getName());
        result.put("params", params != null ? params : new HashMap<>());
        return result;
    }
    
    /**
     * 记录调用成功
     */
    private void recordInvokeSuccess(String capId, long duration) {
        invokeStatsMap.computeIfAbsent(capId, k -> new InvokeStats())
            .recordSuccess(duration);
    }
    
    /**
     * 记录调用失败
     */
    private void recordInvokeFailure(String capId) {
        invokeStatsMap.computeIfAbsent(capId, k -> new InvokeStats())
            .recordFailure();
    }
    
    /**
     * 调用统计内部类
     */
    private static class InvokeStats {
        private final AtomicInteger count = new AtomicInteger(0);
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private volatile long totalTime = 0;
        
        void recordSuccess(long duration) {
            count.incrementAndGet();
            totalTime += duration;
        }
        
        void recordFailure() {
            count.incrementAndGet();
            failureCount.incrementAndGet();
        }
        
        int getCount() {
            return count.get();
        }
        
        long getAverageTime() {
            int c = count.get();
            return c > 0 ? totalTime / c : 0;
        }
    }
}
