/**
 * $RCSfile: MCPServiceFactory.java,v $
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
package net.ooder.ai.factory;


import net.ooder.ai.bean.MCPClientAnnotationBean;
import net.ooder.annotation.ProtocolType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP服务工厂类，管理MCP客户端服务实例
 */

public class MCPServiceFactory  {
    private static volatile MCPServiceFactory instance;
    private final Map<String, MCPClientAnnotationBean> serviceCache;

    private MCPServiceFactory() {
        serviceCache = new ConcurrentHashMap<>();
        initDefaultServices();
    }

    /**
     * 获取工厂单例实例
     */
    public static MCPServiceFactory getInstance() {
        if (instance == null) {
            synchronized (MCPServiceFactory.class) {
                if (instance == null) {
                    instance = new MCPServiceFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化默认MCP服务
     */
    private void initDefaultServices() {
        // 注册STDIO和SSE服务示例
        registerStdioService();
        registerSseService();
    }

    /**
     * 注册STDIO协议服务
     */
    private void registerStdioService() {
        MCPClientAnnotationBean stdioService = new MCPClientAnnotationBean();
        stdioService.setId("stdio-service");
        stdioService.setServiceId("mcp-stdio");
        stdioService.setProtocol(ProtocolType.STDIO);
        stdioService.setVersion("1.0.0");
        stdioService.setDescription("MCP标准输入输出服务");
        stdioService.setTimeout(30000);
        serviceCache.put(stdioService.getId(), stdioService);
    }

    /**
     * 注册SSE协议服务
     */
    private void registerSseService() {
        MCPClientAnnotationBean sseService = new MCPClientAnnotationBean();
        sseService.setId("sse-service");
        sseService.setServiceId("mcp-sse");
        sseService.setProtocol (ProtocolType.SSE);
        sseService.setVersion("1.0.0");
        sseService.setDescription("MCP服务器推送事件服务");
        sseService.setTimeout(60000);
        serviceCache.put(sseService.getId(), sseService);
    }

    /**
     * 注册MCP服务
     */
    public void registerService(MCPClientAnnotationBean service) {
        if (service != null && service.getId() != null) {
            serviceCache.put(service.getId(), service);
        }
    }

    /**
     * 获取MCP服务
     */
    public MCPClientAnnotationBean getService(String serviceId) {
        return serviceCache.get(serviceId);
    }
}