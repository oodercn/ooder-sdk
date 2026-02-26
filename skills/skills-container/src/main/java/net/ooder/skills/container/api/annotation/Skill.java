package net.ooder.skills.container.api.annotation;

import java.lang.annotation.*;

/**
 * Skill 注解
 * 用于声明一个类是 Skill 实现
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Skill {

    /**
     * Skill ID
     */
    String id();

    /**
     * Skill 名称
     */
    String name();

    /**
     * 版本
     */
    String version();

    /**
     * 描述
     */
    String description() default "";

    /**
     * 提供的能力接口
     */
    Class<?>[] capabilities() default {};

    /**
     * 依赖的其他 Skills
     */
    String[] dependencies() default {};
}
