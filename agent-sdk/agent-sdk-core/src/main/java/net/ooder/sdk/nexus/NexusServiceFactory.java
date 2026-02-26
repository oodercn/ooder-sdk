package net.ooder.sdk.nexus;

import net.ooder.sdk.nexus.impl.NexusServiceImpl;
import net.ooder.sdk.nexus.spi.DefaultProtocolProvider;
import net.ooder.sdk.nexus.spi.ProtocolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * NexusService 工厂类，支持 SPI 机制加载协议实现。
 * 
 * 加载优先级：
 * 1. 通过 SPI 机制查找 ProtocolProvider 实现
 * 2. 如果没有找到，使用 SDK 内置的默认实现
 */
public class NexusServiceFactory {
    
    private static final Logger log = LoggerFactory.getLogger(NexusServiceFactory.class);
    
    private static volatile ProtocolProvider cachedProvider;
    private static final Object lock = new Object();
    
    /**
     * 创建 NexusService 实例，自动通过 SPI 加载协议实现
     */
    public static NexusService create() {
        ProtocolProvider provider = loadProvider();
        return new NexusServiceImpl(provider);
    }
    
    /**
     * 使用指定的 ProtocolProvider 创建 NexusService 实例
     */
    public static NexusService create(ProtocolProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("ProtocolProvider cannot be null");
        }
        return new NexusServiceImpl(provider);
    }
    
    /**
     * 通过 SPI 机制加载 ProtocolProvider（Java 8 兼容版本）
     */
    private static ProtocolProvider loadProvider() {
        if (cachedProvider != null) {
            return cachedProvider;
        }
        
        synchronized (lock) {
            if (cachedProvider != null) {
                return cachedProvider;
            }
            
            ServiceLoader<ProtocolProvider> loader = ServiceLoader.load(ProtocolProvider.class);
            
            // Java 8 兼容：使用迭代器而不是 stream
            List<ProtocolProvider> providers = new ArrayList<>();
            for (ProtocolProvider provider : loader) {
                providers.add(provider);
            }
            
            if (!providers.isEmpty()) {
                // 按优先级排序，选择最高的
                cachedProvider = Collections.max(providers, 
                    Comparator.comparingInt(ProtocolProvider::getPriority));
            } else {
                cachedProvider = new DefaultProtocolProvider();
                log.warn("No ProtocolProvider found via SPI, using default implementation");
            }
            
            log.info("Loaded ProtocolProvider: {} (priority: {})", 
                cachedProvider.getProviderName(), 
                cachedProvider.getPriority());
            
            return cachedProvider;
        }
    }
    
    /**
     * 清除缓存的 Provider，用于动态重新加载
     */
    public static void clearCache() {
        synchronized (lock) {
            cachedProvider = null;
            log.debug("ProtocolProvider cache cleared");
        }
    }
    
    /**
     * 检查是否有外部 ProtocolProvider 实现
     */
    public static boolean hasExternalProvider() {
        ServiceLoader<ProtocolProvider> loader = ServiceLoader.load(ProtocolProvider.class);
        return loader.iterator().hasNext();
    }
}
