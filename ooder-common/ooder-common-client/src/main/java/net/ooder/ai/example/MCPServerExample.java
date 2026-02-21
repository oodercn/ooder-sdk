/**
 * $RCSfile: MCPServerExample.java,v $
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


import net.ooder.annotation.AuthorizationType;
import net.ooder.annotation.MCPMethodAnnotation;
import net.ooder.annotation.MCPServerAnnotation;
import net.ooder.annotation.ProtocolType;

/**
 * MCP服务端示例，展示MCPServerAnnotation注解的使用
 */
@MCPServerAnnotation(
    serviceId = "user-service",
    version = "2.1.0",
    protocol = ProtocolType.HTTP,
    host = "0.0.0.0",
    port = 8080,
    authorization = AuthorizationType.JWT,
    enableRateLimit = true,
    maxRequestsPerSecond = 1000, interfaceName = ""
)
public class MCPServerExample {

    /**
     * 获取用户信息接口
     */
    @MCPMethodAnnotation(
        path = "/api/users/{userId}",
       
        timeout = 3000,
        async = false,
        cacheable = true,
        cacheExpireSeconds = 60
    )
    public String getUserInfo(String userId) {
        // 实际业务逻辑
        return "{\"userId\":\"" + userId + "\",\"name\":\"示例用户\"}";
    }

    /**
     * 创建用户接口
     */
    @MCPMethodAnnotation(
        path = "/api/users",
     
        timeout = 5000,
        async = false,
        transactional = true
    )
    public String createUser(String userJson) {
        // 实际业务逻辑
        return "{\"success\":true,\"userId\":\"new-user-id\"}";
    }
}