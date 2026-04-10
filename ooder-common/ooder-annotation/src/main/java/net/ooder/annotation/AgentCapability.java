package net.ooder.annotation;

import java.lang.annotation.*;

/**
 * 标记Agent支持的能力或依赖的外部服务
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(AgentCapabilities.class)
public @interface AgentCapability {
    /** 能力名称 */
    String name();
    
    /** 能力版本 */
    String version() default "1.0.0";
    
    /** 能力提供者 */
    String provider() default "default";
    
    /** 是否为核心能力 */
    boolean required() default true;
    
    /** 配置参数 */
    String[] configs() default {};

    boolean isCore();
}