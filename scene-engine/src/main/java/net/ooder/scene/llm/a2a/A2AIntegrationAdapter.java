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
        
