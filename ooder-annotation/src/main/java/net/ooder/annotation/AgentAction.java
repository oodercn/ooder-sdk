package net.ooder.annotation;

import java.lang.annotation.*;

/**
 * 标记Agent类中的方法为可执行操作
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AgentAction {

    /** 操作名称 */
    String name() default "";
    
    /** 操作描述 */
    String description() default "";
    
    /** 是否异步执行 */
    /**
     * 执行模式
     */
    ExecutionMode executionMode() default ExecutionMode.SYNC;

    /** 超时时间(毫秒) */
    long timeout() default 30000;
    
    /** 输入参数类型 */
    Class<?>[] inputTypes() default {};
    
    /** 输出参数类型 */
    Class<?> outputType() default Object.class;

    String value();
}