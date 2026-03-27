package net.ooder.scene.a2a.mcp;

import net.ooder.scene.a2a.A2AMessage;
import net.ooder.scene.a2a.A2AMessageType;
import net.ooder.scene.a2a.A2ARequest;
import net.ooder.scene.a2a.A2AResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP 消息转换器
 *
 * <p>负责 A2A 消息与 MCP 消息之间的转换。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MCPMessageConverter {

    private static final String MCP_METHOD_TASK = "task.execute";
    private static final String MCP_METHOD_QUERY = "query.execute";
    private static final String MCP_METHOD_DATA = "data.share";
    private static final String MCP_METHOD_NOTIFY = "notification.send";
    private static final String MCP_METHOD_HEARTBEAT = "heartbeat";
    private static final String MCP_METHOD_HANDSHAKE = "handshake";

    public MCPMessage toMCP(A2AMessage a2aMessage) {
        if (a2aMessage == null) {
            return null;
        }
        
        MCPMessage mcpMessage = new MCPMessage();
        mcpMessage.setId(a2aMessage.getMessageId());
        mcpMessage.setFrom(a2aMessage.getFromAgentId());
        mcpMessage.setTo(a2aMessage.getToAgentId());
        mcpMessage.setTimestamp(a2aMessage.getTimestamp());
        
        String method = mapToMCPMethod(a2aMessage.getMessageType());
        mcpMessage.setMethod(method);
        mcpMessage.setType(determineMCPType(a2aMessage));
        
        Map<String, Object> params = new HashMap<>();
        params.put("conversationId", a2aMessage.getConversationId());
        params.put("sceneGroupId", a2aMessage.getSceneGroupId());
        params.put("payload", a2aMessage.getPayload());
        params.put("priority", a2aMessage.getPriority());
        params.put("headers", a2aMessage.getHeaders());
        mcpMessage.setParams(params);
        
        return mcpMessage;
    }

    public A2AMessage toA2A(MCPMessage mcpMessage) {
        if (mcpMessage == null) {
            return null;
        }
        
        A2AMessage a2aMessage = A2AMessage.builder()
                .messageId(mcpMessage.getId())
                .from(mcpMessage.getFrom())
                .to(mcpMessage.getTo())
                .type(mapToA2AType(mcpMessage.getMethod()))
                .build();
        
        a2aMessage.setTimestamp(mcpMessage.getTimestamp());
        
        Map<String, Object> params = mcpMessage.getParams();
        if (params != null) {
            a2aMessage.setConversationId((String) params.get("conversationId"));
            a2aMessage.setSceneGroupId((String) params.get("sceneGroupId"));
            a2aMessage.setPayload(params.get("payload"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> headers = (Map<String, Object>) params.get("headers");
            if (headers != null) {
                a2aMessage.setHeaders(headers);
            }
        }
        
        if (mcpMessage.isError()) {
            a2aMessage.setHeader("error", mcpMessage.getError());
        }
        
        return a2aMessage;
    }

    public MCPMessage toMCPRequest(A2ARequest request) {
        if (request == null) {
            return null;
        }
        
        MCPMessage mcpMessage = MCPMessage.request(MCP_METHOD_TASK, new HashMap<>());
        mcpMessage.setId(request.getRequestId());
        mcpMessage.setFrom(request.getFromAgentId());
        mcpMessage.setTo(request.getToAgentId());
        
        mcpMessage.setParam("action", request.getAction());
        mcpMessage.setParam("parameters", request.getParameters());
        mcpMessage.setParam("sceneGroupId", request.getSceneGroupId());
        mcpMessage.setParam("timeout", request.getTimeout());
        
        return mcpMessage;
    }

    public A2ARequest toA2ARequest(MCPMessage mcpMessage) {
        if (mcpMessage == null || !mcpMessage.isRequest()) {
            return null;
        }
        
        A2ARequest request = new A2ARequest();
        request.setRequestId(mcpMessage.getId());
        request.setFromAgentId(mcpMessage.getFrom());
        request.setToAgentId(mcpMessage.getTo());
        request.setAction((String) mcpMessage.getParam("action"));
        request.setSceneGroupId((String) mcpMessage.getParam("sceneGroupId"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) mcpMessage.getParam("parameters");
        if (params != null) {
            request.setParameters(params);
        }
        
        Object timeout = mcpMessage.getParam("timeout");
        if (timeout instanceof Number) {
            request.setTimeout(((Number) timeout).longValue());
        }
        
        return request;
    }

    public MCPMessage toMCPResponse(A2AResponse response) {
        if (response == null) {
            return null;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", response.isSuccess());
        result.put("data", response.getResult());
        result.put("responseTime", response.getResponseTime());
        
        MCPMessage mcpMessage;
        if (response.isSuccess()) {
            mcpMessage = MCPMessage.response(response.getRequestId(), result);
        } else {
            mcpMessage = MCPMessage.error(response.getRequestId(), 
                    response.getErrorCode() != null ? Integer.parseInt(response.getErrorCode()) : 500,
                    response.getErrorMessage());
        }
        
        mcpMessage.setFrom(response.getFromAgentId());
        mcpMessage.setTo(response.getToAgentId());
        
        return mcpMessage;
    }

    public A2AResponse toA2AResponse(MCPMessage mcpMessage) {
        if (mcpMessage == null) {
            return null;
        }
        
        A2AResponse response = new A2AResponse();
        response.setRequestId(mcpMessage.getId());
        response.setFromAgentId(mcpMessage.getFrom());
        response.setToAgentId(mcpMessage.getTo());
        
        if (mcpMessage.isError()) {
            response.setSuccess(false);
            response.setErrorMessage(mcpMessage.getError().getMessage());
            response.setErrorCode(String.valueOf(mcpMessage.getError().getCode()));
        } else {
            Map<String, Object> result = mcpMessage.getResult();
            if (result != null) {
                response.setSuccess(Boolean.TRUE.equals(result.get("success")));
                response.setResult(result.get("data"));
                
                Object responseTime = result.get("responseTime");
                if (responseTime instanceof Number) {
                    response.setResponseTime(((Number) responseTime).longValue());
                }
            }
        }
        
        return response;
    }

    private String mapToMCPMethod(A2AMessageType a2aType) {
        if (a2aType == null) {
            return MCP_METHOD_NOTIFY;
        }
        
        switch (a2aType) {
            case TASK_REQUEST:
            case TASK_RESPONSE:
            case TASK_STATUS:
                return MCP_METHOD_TASK;
            case QUERY:
                return MCP_METHOD_QUERY;
            case DATA_SHARE:
            case DATA_REQUEST:
                return MCP_METHOD_DATA;
            case HEARTBEAT:
                return MCP_METHOD_HEARTBEAT;
            case HANDSHAKE:
                return MCP_METHOD_HANDSHAKE;
            default:
                return MCP_METHOD_NOTIFY;
        }
    }

    private A2AMessageType mapToA2AType(String mcpMethod) {
        if (mcpMethod == null) {
            return A2AMessageType.NOTIFICATION;
        }
        
        if (mcpMethod.startsWith("task")) {
            return A2AMessageType.TASK_REQUEST;
        } else if (mcpMethod.startsWith("query")) {
            return A2AMessageType.QUERY;
        } else if (mcpMethod.startsWith("data")) {
            return A2AMessageType.DATA_SHARE;
        } else if (mcpMethod.equals(MCP_METHOD_HEARTBEAT)) {
            return A2AMessageType.HEARTBEAT;
        } else if (mcpMethod.equals(MCP_METHOD_HANDSHAKE)) {
            return A2AMessageType.HANDSHAKE;
        }
        
        return A2AMessageType.NOTIFICATION;
    }

    private net.ooder.scene.a2a.mcp.MCPMessageType determineMCPType(A2AMessage a2aMessage) {
        if (a2aMessage.isRequest()) {
            return net.ooder.scene.a2a.mcp.MCPMessageType.REQUEST;
        } else if (a2aMessage.isResponse()) {
            return net.ooder.scene.a2a.mcp.MCPMessageType.RESPONSE;
        }
        return net.ooder.scene.a2a.mcp.MCPMessageType.NOTIFICATION;
    }
}
