package net.ooder.scene.a2a.mcp;

import net.ooder.scene.a2a.A2AMessage;
import net.ooder.scene.a2a.A2AMessageHandler;
import net.ooder.scene.a2a.A2ARequest;
import net.ooder.scene.a2a.A2AResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP Agent
 *
 * <p>处理 MCP 协议的 Agent 实现，负责协议转换和消息处理。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MCPAgent implements A2AMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(MCPAgent.class);

    private final String agentId;
    private final MCPMessageConverter converter;
    private final Map<String, MCPMessageHandler> handlers = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<MCPMessage>> pendingRequests = new ConcurrentHashMap<>();

    public MCPAgent(String agentId) {
        this.agentId = agentId;
        this.converter = new MCPMessageConverter();
    }

    public String getAgentId() {
        return agentId;
    }

    public void registerHandler(String method, MCPMessageHandler handler) {
        handlers.put(method, handler);
        log.debug("MCP handler registered: method={}, handler={}", method, handler.getClass().getSimpleName());
    }

    public void unregisterHandler(String method) {
        handlers.remove(method);
    }

    public MCPMessage sendRequest(MCPMessage request) {
        if (request == null) {
            return null;
        }
        
        request.setFrom(agentId);
        request.setType(net.ooder.scene.a2a.mcp.MCPMessageType.REQUEST);
        
        CompletableFuture<MCPMessage> future = new CompletableFuture<>();
        pendingRequests.put(request.getId(), future);
        
        log.debug("MCP request sent: id={}, method={}, to={}", 
                request.getId(), request.getMethod(), request.getTo());
        
        try {
            return future.get(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            pendingRequests.remove(request.getId());
            log.error("MCP request timeout: id={}", request.getId());
            return MCPMessage.error(request.getId(), 408, "Request timeout");
        }
    }

    public CompletableFuture<MCPMessage> sendRequestAsync(MCPMessage request) {
        if (request == null) {
            return CompletableFuture.completedFuture(null);
        }
        
        request.setFrom(agentId);
        request.setType(net.ooder.scene.a2a.mcp.MCPMessageType.REQUEST);
        
        CompletableFuture<MCPMessage> future = new CompletableFuture<>();
        pendingRequests.put(request.getId(), future);
        
        log.debug("MCP async request sent: id={}, method={}", request.getId(), request.getMethod());
        
        return future;
    }

    public void receiveResponse(MCPMessage response) {
        if (response == null) {
            return;
        }
        
        CompletableFuture<MCPMessage> future = pendingRequests.remove(response.getId());
        if (future != null) {
            future.complete(response);
            log.debug("MCP response received: id={}", response.getId());
        }
    }

    public void receiveMessage(MCPMessage message) {
        if (message == null) {
            return;
        }
        
        log.debug("MCP message received: id={}, method={}, from={}", 
                message.getId(), message.getMethod(), message.getFrom());
        
        if (message.isResponse()) {
            receiveResponse(message);
            return;
        }
        
        MCPMessageHandler handler = handlers.get(message.getMethod());
        if (handler != null) {
            try {
                MCPMessage response = handler.handle(message);
                if (response != null && message.isRequest()) {
                    response.setId(message.getId());
                    response.setTo(message.getFrom());
                    response.setFrom(agentId);
                }
            } catch (Exception e) {
                log.error("MCP handler error: method={}, error={}", message.getMethod(), e.getMessage());
                if (message.isRequest()) {
                    MCPMessage errorResponse = MCPMessage.error(message.getId(), 500, e.getMessage());
                    errorResponse.setTo(message.getFrom());
                    errorResponse.setFrom(agentId);
                }
            }
        } else {
            log.warn("No handler for MCP method: {}", message.getMethod());
            if (message.isRequest()) {
                MCPMessage errorResponse = MCPMessage.error(message.getId(), 404, "Method not found");
                errorResponse.setTo(message.getFrom());
                errorResponse.setFrom(agentId);
            }
        }
    }

    @Override
    public void handle(A2AMessage message) {
        if (message == null) {
            return;
        }
        
        MCPMessage mcpMessage = converter.toMCP(message);
        receiveMessage(mcpMessage);
    }

    @Override
    public boolean canHandle(A2AMessage message) {
        return message != null && 
               (message.getToAgentId() == null || agentId.equals(message.getToAgentId()));
    }

    @Override
    public String getHandlerId() {
        return "MCPAgent-" + agentId;
    }

    public A2AMessage toA2AMessage(MCPMessage mcpMessage) {
        return converter.toA2A(mcpMessage);
    }

    public MCPMessage toMCPMessage(A2AMessage a2aMessage) {
        return converter.toMCP(a2aMessage);
    }

    public A2ARequest toA2ARequest(MCPMessage mcpMessage) {
        return converter.toA2ARequest(mcpMessage);
    }

    public A2AResponse toA2AResponse(MCPMessage mcpMessage) {
        return converter.toA2AResponse(mcpMessage);
    }

    public interface MCPMessageHandler {
        MCPMessage handle(MCPMessage message);
    }
}
