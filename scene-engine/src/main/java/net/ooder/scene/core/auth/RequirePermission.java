package net.ooder.scene.core.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限要求注解
 *
 * <p>用于标记需要特定权限才能访问的方法</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * 需要的权限列表
     */
    String[] value() default {};

    /**
     * 权限检查逻辑：AND（全部需要）或 OR（任一即可）
     */
    Logic logic() default Logic.AND;

    enum Logic {
        AND, OR
    }
}
