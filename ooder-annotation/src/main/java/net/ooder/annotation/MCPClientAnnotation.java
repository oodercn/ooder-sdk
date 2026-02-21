package net.ooder.annotation;

import java.lang.annotation.*;

import net.ooder.annotation.ProtocolType;
import net.ooder.annotation.LoadBalanceStrategy;

/**
 * MCP客户端服务注解
 * 用于标识MCP客户端服务实现类
 */
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MCPClientAnnotation {
    /**
     * 服务ID
     */
    String id();

    /**
     * 目标服务ID
     */
    String serviceId() default "";

    /**
     * 通信协议
     */
    ProtocolType protocol() default ProtocolType.HTTP;

    /**
     * 负载均衡策略
     */
    LoadBalanceStrategy loadBalanceStrategy() default LoadBalanceStrategy.RANDOM;

    /**
     * 服务版本号
     */
    String version() default "1.0.0";

    /**
     * 服务描述
     */
    String description() default "";

    /**
     * 超时时间(毫秒)
     */
    int timeout() default 30000;

    /**
     * 是否启用负载均衡
     */
    LoadBalanceStrategy loadBalance() default LoadBalanceStrategy.RANDOM;

    int retries();

    Class fallbackClass();

    boolean enableCircuitBreaker();
}