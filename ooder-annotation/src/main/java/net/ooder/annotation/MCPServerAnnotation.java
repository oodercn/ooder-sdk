package net.ooder.annotation;

import java.lang.annotation.*;


/**
 * MCP服务端注解
 * 用于标识MCP服务端接口或实现类
 */
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MCPServerAnnotation {
    /**
     * 服务ID
     */
    String serviceId();

    /**
     * 协议类型
     */
    ProtocolType protocol() default ProtocolType.HTTP;

    /**
     * 主机地址
     */
    String host() default "0.0.0.0";

    /**
     * 端口号
     */
    int port() default 8080;

    /**
     * 授权类型
     */
    AuthorizationType authorization() default AuthorizationType.NONE;

    /**
     * 是否启用限流
     */
    boolean enableRateLimit() default false;

    /**
     * 每秒最大请求数
     */
    int maxRequestsPerSecond() default 1000;

    /**
     * 服务接口名称
     */
    String interfaceName();

    /**
     * 服务版本号
     */
    String version() default "1.0.0";

    /**
     * 服务分组
     */
    String group() default "default";

    /**
     * 是否需要权限验证
     */
    boolean authRequired() default true;

    /**
     * 服务权重
     */
    int weight() default 1;
}