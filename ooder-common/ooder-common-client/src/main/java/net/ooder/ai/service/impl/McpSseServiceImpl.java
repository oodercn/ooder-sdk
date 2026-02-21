/**
 * $RCSfile: McpSseServiceImpl.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.ai.service.impl;


import net.ooder.annotation.LoadBalanceStrategy;
import net.ooder.annotation.MCPClientAnnotation;
import net.ooder.annotation.ProtocolType;

import java.util.function.Consumer;

/**
 * MCP SSE协议服务实现
 */
@MCPClientAnnotation(
    fallbackClass = Void.class,
    enableCircuitBreaker = true,
    id = "sse-service",
    serviceId = "mcp-sse",
    protocol = ProtocolType.SSE,
    loadBalanceStrategy = LoadBalanceStrategy.LEAST_CONNECTED,
    version = "1.0.0",
    description = "基于服务器推送事件的MCP服务实现", retries = 0
)
public class McpSseServiceImpl {
    /**
     * 注册SSE事件监听器
     */
    public void registerListener(String eventType, Consumer<String> listener) {
        // SSE协议实现逻辑
        System.out.println("Registered SSE listener for event: " + eventType);
    }

    /**
     * 发送SSE事件
     */
    public void sendEvent(String eventType, String data) {
        // SSE事件发送逻辑
        System.out.println("Sending SSE event [" + eventType + "]: " + data);
    }
}