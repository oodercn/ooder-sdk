package net.ooder.annotation;

import java.lang.annotation.*;

/**
 * AgentCapability注解的容器类，支持重复标注
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AgentCapabilities {
    AgentCapability[] value();
}