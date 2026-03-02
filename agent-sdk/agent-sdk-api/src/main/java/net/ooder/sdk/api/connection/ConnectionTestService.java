package net.ooder.sdk.api.connection;

import java.util.concurrent.CompletableFuture;

/**
 * 连接测试服务接口
 * 提供能力端点连接测试功能
 *
 * @author ooder
 * @since 2.3
 */
public interface ConnectionTestService {
    
    /**
     * 测试能力端点连接
     * @param endpoint 能力端点配置
     * @return 连接测试结果
     */
    CompletableFuture<ConnectionTestResult> testCapabilityEndpoint(CapabilityEndpoint endpoint);
}
