/**
 * $RCSfile: MCPClientExample.java,v $
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
package net.ooder.ai.example;


import net.ooder.annotation.LoadBalanceStrategy;
import net.ooder.annotation.MCPClientAnnotation;
import net.ooder.annotation.ProtocolType;

/**
 * MCP客户端示例，展示MCPClientAnnotation注解的使用
 */
@MCPClientAnnotation(
    serviceId = "user-service",
    version = "2.1.0",
    protocol = ProtocolType.HTTP,
    loadBalance = LoadBalanceStrategy.ROUND_ROBIN,
    timeout = 5000,
    retries = 2,
    fallbackClass = UserServiceFallback.class,
    enableCircuitBreaker = true, id = "user-service"
)
public class MCPClientExample {
    // 客户端方法实现
    public String getUserInfo(String userId) {
        // 实际调用逻辑
        return "用户信息...";
    }
}
