package net.ooder.annotation;

import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MCPWorkflow {
    /** MCP服务名称 */
    String serviceName();
    
    /** 方法名称 */
    String method();
    
    /** 协议类型 */
    ProtocolType protocol() default ProtocolType.HTTP;
    
    /** 步骤超时时间 */
    int timeout() default 30000;
    
    /** 失败重试次数 */
    int retryCount() default 1;
    
    /** 异步回调步骤ID */
    String callbackStepId() default "";
}