/**
 * $RCSfile: McpStdioServiceImpl.java,v $
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

/**
 * MCP STDIO协议服务实现
 */
@MCPClientAnnotation(
    id = "stdio-service",
    serviceId = "mcp-stdio",
    protocol = ProtocolType.STDIO,
    loadBalanceStrategy = LoadBalanceStrategy.ROUND_ROBIN,
    version = "1.0.0",
    description = "基于标准输入输出的MCP服务实现", enableCircuitBreaker = false, fallbackClass = Void.class, retries = 0
)
public class McpStdioServiceImpl {
    /**
     * 发送STDIO请求
     */
    public String sendRequest(String data) {
        // STDIO协议实现逻辑
        System.out.println("STDIO Request: " + data);
        return "STDIO Response: " + data.toUpperCase();
    }
}