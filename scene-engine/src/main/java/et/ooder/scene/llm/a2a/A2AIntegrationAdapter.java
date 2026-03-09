package net.ooder.scene.llm.a2a;

import net.ooder.scene.llm.command.*;
import net.ooder.scene.llm.context.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
 * @since 2.3.1
 */
public class A2AIntegrationAdapter {
    
    private static final Logger log = LoggerFactory.getLogger(A2AIntegrationAdapter.class);
    
    private final LlmContextRegistry contextRegistry;
    private final ContextTransferHandler transferHandler;
    
    // 外部依赖（由AGENT-SDK提供）
    private A2AService a2aService;
    
    public A2AIntegrationAdapter(LlmContextRegistry contextRegistry, 
                                  ContextTransferHandler transferHandler) {
        this.contextRegistry = contextRegistry;
        this.transferHandler = transferHandler;
    }
    
    /**
     * 设置 A2A 服务（由 AGENT-SDK 注入）
     */
    public void setA2AService(A2AService a2aService) {
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
    public CrossSceneResult callCrossScene(String sourceContextId, 
                                            String targetSceneId,
                                            CrossSceneRequest request) {
        if (a2aService == null) {
            throw new IllegalStateException("A2A Service not available");
        }
        
        log.debug("Cross-scene call: sourceContextId={}, targetSceneId={}", 
            sourceContextId, targetSceneId);
        
        // 1. 获取源上下文
        LlmSceneContext sourceContext = contextRegistry.get(sourceContextId);
        if (sourceContext == null) {
            throw new CrossSceneException("Source context not found: " + sourceContextId);
        }
        
        // 2. 准备上下文传递
        ContextTransfer transfer = transferHandler.prepareTransfer(
            sourceContext,
            request.getTransferMode(),
            request.getIncludedParts()
        );
        
        // 3. 构建 Command
        Command command = buildCommand(sourceContext, targetSceneId, transfer, request);
        
        // 4. 发送 Command
        try {
            CommandResponse response = a2aService.sendCommand(command);
            
            // 5. 处理响应
            return processResponse(response, targetSceneId);
        } catch (Exception e) {
            log.error("Cross-scene call failed: {}", e.getMessage());
            throw new CrossSceneException("Cross-scene call failed", e);
        }
    }
    
    /**
     * 处理接收到的跨场景调用
     * 
     * @param command 接收到的命令
     * @return 处理结果
     */
    public ReceivedCallResult handleReceivedCall(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command must not be null");
        }
        
        log.debug("Received cross-scene call: commandId={}, sourceSceneId={}", 
            command.getCommandId(), command.getSourceSceneId());
        
        ContextTransfer transfer = command.getContextTransfer();
        if (transfer == null) {
            throw new CrossSceneException("No context transfer in command");
        }
        
        // 1. 验证传递
        if (!validateTransfer(transfer)) {
            throw new CrossSceneException("Context transfer validation failed");
        }
        
        // 2. 接收上下文
        LlmSceneContext receivedContext = transferHandler.receiveTransfer(
            transfer, 
            command.getTargetSceneId()
        );
        
        // 3. 提取 payload
        Map<String, Object> payload = extractPayload(command);
        
        return ReceivedCallResult.builder()
            .context(receivedContext)
            .payload(payload)
            .sourceSceneId(command.getSourceSceneId())
            .commandId(command.getCommandId())
            .build();
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
        if (a2aService == null) {
            throw new IllegalStateException("A2A Service not available");
        }
        
        CommandResponse response = CommandResponse.builder()
            .responseId(generateId())
            .commandId(originalCommandId)
            .success(success)
            .result(result)
            .timestamp(System.currentTimeMillis())
            .build();
        
        a2aService.sendResponse(response);
    }
    
    /**
     * 构建 Command
     */
    private Command buildCommand(LlmSceneContext sourceContext,
                                  String targetSceneId,
                                  ContextTransfer transfer,
                                  CrossSceneRequest request) {
        CommandHeader header = CommandHeader.builder()
            .commandId(generateId())
            .commandType(A2ACommandType.CROSS_SCENE_CALL.name())
            .timestamp(System.currentTimeMillis())
            .sourceSceneId(sourceContext.getSceneId())
            .targetSceneId(targetSceneId)
            .build();
        
        CommandBody body = CommandBody.builder()
            .payload(request.getPayload())
            .contextTransfer(transfer)
            .transferMode(request.getTransferMode())
            .build();
        
        CommandMetadata metadata = CommandMetadata.builder()
            .priority(request.getPriority())
            .timeoutMillis(request.getTimeoutMillis())
            .retryCount(request.getRetryCount())
            .build();
        
        return Command.builder()
            .header(header)
            .body(body)
            .metadata(metadata)
            .build();
    }
    
    /**
     * 处理响应
     */
    private CrossSceneResult processResponse(CommandResponse response, String targetSceneId) {
        if (response == null) {
            throw new CrossSceneException("Empty response");
        }
        
        if (!response.isSuccess()) {
            throw new CrossSceneException("Remote call failed: " + response.getErrorMessage());
        }
        
        return CrossSceneResult.builder()
            .success(true)
            .data(response.getResult())
            .targetSceneId(targetSceneId)
            .responseId(response.getResponseId())
            .build();
    }
    
    /**
     * 验证传递
     */
    private boolean validateTransfer(ContextTransfer transfer) {
        if (transfer == null) {
            return false;
        }
        
        // 基础验证
        if (transfer.getSourceContextId() == null) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 提取 payload
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractPayload(Command command) {
        if (command.getBody() == null) {
            return new HashMap<>();
        }
        
        Object payload = command.getBody().getPayload();
        if (payload instanceof Map) {
            return (Map<String, Object>) payload;
        }
        
        return new HashMap<>();
    }
    
    /**
     * 生成唯一ID
     */
    private String generateId() {
        return "cmd-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
    
    // ============ 内部类 ============
    
    /**
     * A2A 服务接口（由 AGENT-SDK 实现）
     */
    public interface A2AService {
        /**
         * 发送命令
         */
        CommandResponse sendCommand(Command command);
        
        /**
         * 发送响应
         */
        void sendResponse(CommandResponse response);
    }
    
    /**
     * 跨场景请求
     */
    public static class CrossSceneRequest {
        private ContextTransfer.TransferMode transferMode = ContextTransfer.TransferMode.REFERENCE;
        private Set<ContextTransfer.ContextPart> includedParts;
        private Map<String, Object> payload;
        private int priority = 5;
        private long timeoutMillis = 30000;
        private int retryCount = 3;
        
        public static Builder builder() {
            return new Builder();
        }
        
        // Getters and Setters
        public ContextTransfer.TransferMode getTransferMode() { return transferMode; }
        public void setTransferMode(ContextTransfer.TransferMode transferMode) { this.transferMode = transferMode; }
        
        public Set<ContextTransfer.ContextPart> getIncludedParts() { return includedParts; }
        public void setIncludedParts(Set<ContextTransfer.ContextPart> includedParts) { this.includedParts = includedParts; }
        
        public Map<String, Object> getPayload() { return payload; }
        public void setPayload(Map<String, Object> payload) { this.payload = payload; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        public long getTimeoutMillis() { return timeoutMillis; }
        public void setTimeoutMillis(long timeoutMillis) { this.timeoutMillis = timeoutMillis; }
        
        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
        
        public static class Builder {
            private CrossSceneRequest request = new CrossSceneRequest();
            
            public Builder transferMode(ContextTransfer.TransferMode mode) {
                request.setTransferMode(mode);
                return this;
            }
            
            public Builder includedParts(Set<ContextTransfer.ContextPart> parts) {
                request.setIncludedParts(parts);
                return this;
            }
            
            public Builder payload(Map<String, Object> payload) {
                request.setPayload(payload);
                return this;
            }
            
            public Builder priority(int priority) {
                request.setPriority(priority);
                return this;
            }
            
            public Builder timeoutMillis(long timeoutMillis) {
                request.setTimeoutMillis(timeoutMillis);
                return this;
            }
            
            public Builder retryCount(int retryCount) {
                request.setRetryCount(retryCount);
                return this;
            }
            
            public CrossSceneRequest build() {
                return request;
            }
        }
    }
    
    /**
     * 跨场景结果
     */
    public static class CrossSceneResult {
        private boolean success;
        private Object data;
        private String targetSceneId;
        private String responseId;
        private String errorMessage;
        
        public static Builder builder() {
            return new Builder();
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public String getTargetSceneId() { return targetSceneId; }
        public void setTargetSceneId(String targetSceneId) { this.targetSceneId = targetSceneId; }
        
        public String getResponseId() { return responseId; }
        public void setResponseId(String responseId) { this.responseId = responseId; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public static class Builder {
            private CrossSceneResult result = new CrossSceneResult();
            
            public Builder success(boolean success) {
                result.setSuccess(success);
                return this;
            }
            
            public Builder data(Object data) {
                result.setData(data);
                return this;
            }
            
            public Builder targetSceneId(String targetSceneId) {
                result.setTargetSceneId(targetSceneId);
                return this;
            }
            
            public Builder responseId(String responseId) {
                result.setResponseId(responseId);
                return this;
            }
            
            public Builder errorMessage(String errorMessage) {
                result.setErrorMessage(errorMessage);
                return this;
            }
            
            public CrossSceneResult build() {
                return result;
            }
        }
    }
    
    /**
     * 接收调用结果
     */
    public static class ReceivedCallResult {
        private LlmSceneContext context;
        private Map<String, Object> payload;
        private String sourceSceneId;
        private String commandId;
        
        public static Builder builder() {
            return new Builder();
        }
        
        // Getters and Setters
        public LlmSceneContext getContext() { return context; }
        public void setContext(LlmSceneContext context) { this.context = context; }
        
        public Map<String, Object> getPayload() { return payload; }
        public void setPayload(Map<String, Object> payload) { this.payload = payload; }
        
        public String getSourceSceneId() { return sourceSceneId; }
        public void setSourceSceneId(String sourceSceneId) { this.sourceSceneId = sourceSceneId; }
        
        public String getCommandId() { return commandId; }
        public void setCommandId(String commandId) { this.commandId = commandId; }
        
        public static class Builder {
            private ReceivedCallResult result = new ReceivedCallResult();
            
            public Builder context(LlmSceneContext context) {
                result.setContext(context);
                return this;
            }
            
            public Builder payload(Map<String, Object> payload) {
                result.setPayload(payload);
                return this;
            }
            
            public Builder sourceSceneId(String sourceSceneId) {
                result.setSourceSceneId(sourceSceneId);
                return this;
            }
            
            public Builder commandId(String commandId) {
                result.setCommandId(commandId);
                return this;
            }
            
            public ReceivedCallResult build() {
                return result;
            }
        }
    }
    
    /**
     * 跨场景异常
     */
    public static class CrossSceneException extends RuntimeException {
        public CrossSceneException(String message) {
            super(message);
        }
        
        public CrossSceneException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
