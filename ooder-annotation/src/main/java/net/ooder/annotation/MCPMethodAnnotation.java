package net.ooder.annotation;

import java.lang.annotation.*;


/**
 * MCP方法注解
 * 用于标识MCP服务中的具体方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MCPMethodAnnotation {
    /**
     * 请求路径
     */
    String path() default "";

    /**
     * HTTP方法
     */
    HttpMethod method() default HttpMethod.GET;

    /**
     * 超时时间(毫秒)
     */
    int timeout() default 3000;

    /**
     * 是否异步执行
     */
    boolean async() default false;

    /**
     * 是否开启事务
     */
    boolean transactional() default false;

    /**
     * 是否缓存
     */
    boolean cacheable() default false;

    /**
     * 缓存过期时间(秒)
     */
    int cacheExpireSeconds() default 60;

    /**
     * 重试次数
     */
    int retryCount() default 0;

    /**
     * 缓存时间(秒)，0表示不缓存
     */
    int cacheTime() default 0;
}