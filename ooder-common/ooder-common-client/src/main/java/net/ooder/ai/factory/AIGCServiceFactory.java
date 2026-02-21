/**
 * $RCSfile: AIGCServiceFactory.java,v $
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


import net.ooder.ai.bean.AIGCModelBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AIGC服务工厂类，采用单例模式管理AIGC服务实例
 */
public class AIGCServiceFactory {
    private static volatile AIGCServiceFactory instance;
    private final Map<String, AIGCModelBean> serviceCache;

    private AIGCServiceFactory() {
        serviceCache = new ConcurrentHashMap<>();
        initDefaultServices();
    }

    /**
     * 获取工厂单例实例
     */
    public static AIGCServiceFactory getInstance() {
        if (instance == null) {
            synchronized (AIGCServiceFactory.class) {
                if (instance == null) {
                    instance = new AIGCServiceFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化默认AIGC服务
     */
    private void initDefaultServices() {
        // 可以通过注解扫描自动注册服务
        // 此处简化为手动注册示例
    }

    /**
     * 注册AIGC服务
     */
    public void registerService(AIGCModelBean service) {
        if (service != null && service.getModelId() != null) {
            serviceCache.put(service.getModelId(), service);
        }
    }

    /**
     * 获取AIGC服务
     */
    public AIGCModelBean getService(String modelId) {
        return serviceCache.get(modelId);
    }

    /**
     * 获取默认AIGC服务
     */
    public AIGCModelBean getDefaultService() {
        return serviceCache.values().stream()
                .filter(AIGCModelBean::isDefault)
                .findFirst()
                .orElse(null);
    }
}