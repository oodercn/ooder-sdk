package net.ooder.scene.llm.a2a;

import net.ooder.scene.llm.command.ContextTransfer;
import net.ooder.scene.llm.context.ContextTransferHandler;
import net.ooder.scene.llm.context.LlmSceneContext;
import net.ooder.scene.llm.context.LlmContextRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A2A 集成适配器
 * 
 * <p>桥接 Engine 和 AGENT-SDK，处理跨场景调用和上下文传递。</p>
 * 
 * <p>主要职责：</p>
 * <ul>
 *   <li>跨场景调用封装</li>
 *   <li>上下文传递处理</li>
 *   <li>错误处理和重试</li>
 *   <li>超时控制</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class A2AIntegrationAdapter {
    
    private static final Logger log = LoggerFactory.getLogger(A2AIntegrationAdapter.class);
    
    // 默认超时时间（毫秒）
    private static final long DEFAULT_TIMEOUT_MS = 30000;
    
    // AGENT-SDK A2A 服务接口（通过 setA2AService 注入）
    private Object a2aService;
    
    private final ContextTransferHandler transferHandler;
    private final LlmContextRegistry contextRegistry;
    private final A2AContextTransferConfig transferConfig;
    
    public A2AIntegrationAdapter(ContextTransferHandler transferHandler, 
                                  LlmContextRegistry contextRegistry) {
        this.transferHandler = transferHandler;
        this.contextRegistry = contextRegistry;
        this.transferConfig = new A2AContextTransferConfig();
        log.info("A2AIntegrationAdapter initialized");
    }
    
    /**
     * 设置 A2A 服务（由 AGENT-SDK 注入）
     */
    public void setA2AService(Object a2aService) {
        this.a2aService = a2aService;
        log.info("A2A Service injected");
    }
    
    /**
     * 跨场景调用
     * 
     * @param sourceContextId 源上下文ID
     * @param targetSceneId 目标场景ID
     * @param request 跨场景请求
     * @return 调用结果
     */
    public CompletableFuture<Map<String, Object>> callCrossScene(String sourceContextId, 
                                                                  String targetSceneId,
                                                                  Map<String, Object> request) {
        return callCrossScene(sourceContextId, targetSceneId, request, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * 跨场景调用（带超时）
     * 
     * @param sourceContextId 源上下文ID
     * @param targetSceneId 目标场景ID
     * @param request 跨场景请求
     * @param timeoutMs 超时时间（毫秒）
     * @return 调用结果
     */
    public CompletableFuture<Map<String, Object>> callCrossScene(String sourceContextId, 
                                                                  String targetSceneId,
                                                                  Map<String, Object> request,
                                                                  long timeoutMs) {
        log.info("Cross-scene call: sourceContextId={}, targetSceneId={}", 
            sourceContextId, targetSceneId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. 获取源上下文
                LlmSceneContext sourceContext = contextRegistry.get(sourceContextId);
                if (sourceContext == null) {
                    throw new IllegalArgumentException("Source context not found: " + sourceContextId);
                }
                
                // 2. 准备上下文传递
                ContextTransfer transfer = transferHandler.prepareTransfer(
                    sourceContext,
                    transferConfig.getDefaultMode(),
                    transferConfig.getDefaultIncludedParts()
                );
                
                // 3. 构建 A2A 命令
                A2ACommand command = buildA2ACommand(sourceContext, targetSceneId, request, transfer);
                
                // 4. 发送命令（如果 A2A 服务可用）
                if (a2aService != null) {
                    return sendViaA2AService(command, timeoutMs);
                } else {
                    // 降级处理：本地模拟
                    log.warn("A2A Service not available, using local fallback");
                    return handleLocalFallback(command);
                }
                
            } catch (Exception e) {
                log.error("Cross-scene call failed", e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", e.getMessage());
                errorResult.put("sourceContextId", sourceContextId);
                errorResult.put("targetSceneId", targetSceneId);
                return errorResult;
            }
        });
    }
    
    /**
     * 构建 A2A 命令
     */
    private A2ACommand buildA2ACommand(LlmSceneContext sourceContext, 
                                        String targetSceneId,
                                        Map<String, Object> request,
                                        ContextTransfer transfer) {
        return A2ACommand.builder()
            .commandId(generateId())
            .timestamp(System.currentTimeMillis())
            .header(A2ACommand.CommandHeader.builder()
                .commandType(A2ACommandType.LLM_CONTEXT_SHARE)
                .version("2.3.1")
                .build())
            .body(A2ACommand.CommandBody.builder()
                .source(AgentInfo.of(sourceContext.getAgentId(), sourceContext.getSceneId()))
                .target(AgentInfo.of(null, targetSceneId))
                .payload(request)
                .build())
            .contextTransfer(transfer)
            .build();
    }
    
    /**
     * 通过 A2A 服务发送命令
     */
    private Map<String, Object> sendViaA2AService(A2ACommand command, long timeoutMs) {
        // TODO: 实际调用 AGENT-SDK 的 A2A 服务
        // 这里需要根据 AGENT-SDK 的实际 API 进行调整
        
        log.debug("Sending via A2A service: commandId={}", command.getCommandId());
        
        // 模拟调用
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "A2A call simulated");
        result.put("commandId", command.getCommandId());
        result.put("timestamp", System.currentTimeMillis());
        
        return result;
    }
    
    /**
     * 本地降级处理
     */
    private Map<String, Object> handleLocalFallback(A2ACommand command) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Local fallback - A2A service not available");
        result.put("commandId", command.getCommandId());
        result.put("fallback", true);
        return result;
    }
    
    /**
     * 处理接收到的跨场景调用
     * 
     * @param command 接收到的命令
     * @return 处理结果
     */
    public Map<String, Object> handleReceivedCall(A2ACommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command must not be null");
        }
        
        log.info("Received cross-scene call: commandId={}, type={}", 
            command.getCommandId(), 
            command.getHeader().getCommandType());
        
        try {
            // 1. 接收上下文传递
            if (command.getContextTransfer() != null) {
                String targetSceneId = command.getBody().getTarget().getSceneId();
                LlmSceneContext receivedContext = transferHandler.receiveTransfer(
                    command.getContextTransfer(), 
                    targetSceneId
                );
                contextRegistry.register(receivedContext);
            }
            
            // 2. 处理命令
            Map<String, Object> result = processCommand(command);
            
            // 3. 发送响应
            sendResponse(command.getCommandId(), result, true);
            
            return result;
            
        } catch (Exception e) {
            log.error("Failed to handle received call", e);
            sendResponse(command.getCommandId(), e.getMessage(), false);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }
    
    /**
     * 处理命令
     */
    private Map<String, Object> processCommand(A2ACommand command) {
        Map<String, Object> result = new HashMap<>();
        
        switch (command.getHeader().getCommandType()) {
            case LLM_CONTEXT_SHARE:
                result.put("success", true);
                result.put("message", "Context shared successfully");
                result.put("receivedPayload", command.getBody().getPayload());
                break;
                
            case LLM_FUNCTION_CALL:
                // TODO: 执行函数调用
                result.put("success", true);
                result.put("message", "Function call executed");
                break;
                
            default:
                result.put("success", false);
                result.put("message", "Unknown command type: " + command.getHeader().getCommandType());
        }
        
        return result;
    }
    
    /**
     * 发送响应
     * 
     * @param originalCommandId 原始命令ID
     * @param result 处理结果
     * @param success 是否成功
     */
    public void sendResponse(String originalCommandId, 
                             Object result, 
                             boolean success) {
        log.debug("Sending response: commandId={}, success={}", originalCommandId, success);
        
        // TODO: 实现实际的响应发送
        // 这里需要调用 AGENT-SDK 的响应发送接口
    }
    
    /**
     * 生成唯一ID
     */
    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * A2A 命令类型
     */
    public enum A2ACommandType {
        LLM_CONTEXT_SHARE,
        LLM_FUNCTION_CALL,
        LLM_QUERY,
        LLM_RESPONSE
    }
    
    /**
     * A2A 命令
     */
    public static class A2ACommand {
        private String commandId;
        private long timestamp;
        private CommandHeader header;
        private CommandBody body;
        private ContextTransfer contextTransfer;
        
        public static Builder builder() {
            return new Builder();
        }
        
        // Getters and Setters
        public String getCommandId() { return commandId; }
        public void setCommandId(String commandId) { this.commandId = commandId; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public CommandHeader getHeader() { return header; }
        public void setHeader(CommandHeader header) { this.header = header; }
        public CommandBody getBody() { return body; }
        public void setBody(CommandBody body) { this.body = body; }
        public ContextTransfer getContextTransfer() { return contextTransfer; }
        public void setContextTransfer(ContextTransfer contextTransfer) { this.contextTransfer = contextTransfer; }
        
        public static class Builder {
            private A2ACommand command = new A2ACommand();
            
            public Builder commandId(String commandId) {
                command.setCommandId(commandId);
                return this;
            }
            
            public Builder timestamp(long timestamp) {
                command.setTimestamp(timestamp);
                return this;
            }
            
            public Builder header(CommandHeader header) {
                command.setHeader(header);
                return this;
            }
            
            public Builder body(CommandBody body) {
                command.setBody(body);
                return this;
            }
            
            public Builder contextTransfer(ContextTransfer transfer) {
                command.setContextTransfer(transfer);
                return this;
            }
            
            public A2ACommand build() {
                return command;
            }
        }
        
        public static class CommandHeader {
            private A2ACommandType commandType;
            private String version;
            
            public static Builder builder() {
                return new Builder();
            }
            
            public A2ACommandType getCommandType() { return commandType; }
            public void setCommandType(A2ACommandType commandType) { this.commandType = commandType; }
            public String getVersion() { return version; }
            public void setVersion(String version) { this.version = version; }
            
            public static class Builder {
                private CommandHeader header = new CommandHeader();
                
                public Builder commandType(A2ACommandType type) {
                    header.setCommandType(type);
                    return this;
                }
                
                public Builder version(String version) {
                    header.setVersion(version);
                    return this;
                }
                
                public CommandHeader build() {
                    return header;
                }
            }
        }
        
        public static class CommandBody {
            private AgentInfo source;
            private AgentInfo target;
            private Map<String, Object> payload;
            
            public static Builder builder() {
                return new Builder();
            }
            
            public AgentInfo getSource() { return source; }
            public void setSource(AgentInfo source) { this.source = source; }
            public AgentInfo getTarget() { return target; }
            public void setTarget(AgentInfo target) { this.target = target; }
            public Map<String, Object> getPayload() { return payload; }
            public void setPayload(Map<String, Object> payload) { this.payload = payload; }
            
            public static class Builder {
                private CommandBody body = new CommandBody();
                
                public Builder source(AgentInfo source) {
                    body.setSource(source);
                    return this;
                }
                
                public Builder target(AgentInfo target) {
                    body.setTarget(target);
                    return this;
                }
                
                public Builder payload(Map<String, Object> payload) {
                    body.setPayload(payload);
                    return this;
                }
                
                public CommandBody build() {
                    return body;
                }
            }
        }
    }
    
    /**
     * Agent 信息
     */
    public static class AgentInfo {
        private String agentId;
        private String sceneId;
        
        public static AgentInfo of(String agentId, String sceneId) {
            AgentInfo info = new AgentInfo();
            info.agentId = agentId;
            info.sceneId = sceneId;
            return info;
        }
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    }
    
    /**
     * A2A 上下文传递配置
     */
    public static class A2AContextTransferConfig {
        private ContextTransfer.TransferMode defaultMode = ContextTransfer.TransferMode.SELECTIVE;
        private Set<ContextTransfer.ContextPart> defaultIncludedParts = new HashSet<>(Arrays.asList(
            ContextTransfer.ContextPart.USER_CONTEXT,
            ContextTransfer.ContextPart.KNOWLEDGE_CONTEXT,
            ContextTransfer.ContextPart.NLP_CONTEXT,
            ContextTransfer.ContextPart.CONVERSATION_MEMORY
        ));
        private Set<ContextTransfer.ContextPart> defaultExcludedParts = new HashSet<>(Arrays.asList(
            ContextTransfer.ContextPart.SECURITY_CONTEXT
        ));
        private long maxTransferSize = 65536; // 64KB
        
        public ContextTransfer.TransferMode getDefaultMode() { return defaultMode; }
        public void setDefaultMode(ContextTransfer.TransferMode defaultMode) { this.defaultMode = defaultMode; }
        public Set<ContextTransfer.ContextPart> getDefaultIncludedParts() { return defaultIncludedParts; }
        public void setDefaultIncludedParts(Set<ContextTransfer.ContextPart> parts) { this.defaultIncludedParts = parts; }
        public Set<ContextTransfer.ContextPart> getDefaultExcludedParts() { return defaultExcludedParts; }
        public void setDefaultExcludedParts(Set<ContextTransfer.ContextPart> parts) { this.defaultExcludedParts = parts; }
        public long getMaxTransferSize() { return maxTransferSize; }
        public void setMaxTransferSize(long maxTransferSize) { this.maxTransferSize = maxTransferSize; }
    }
}
