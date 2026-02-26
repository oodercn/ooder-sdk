package net.ooder.skills.container.api.annotation;

import java.lang.annotation.*;

/**
 * 能力注解
 * 用于声明一个方法是能力实现
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Capability {

    /**
     * 能力 ID
     */
    String id();

    /**
     * 能力名称
     */
    String name();

    /**
     * 能力分类
     */
    String category();

    /**
     * 描述
     */
    String description() default "";
}
