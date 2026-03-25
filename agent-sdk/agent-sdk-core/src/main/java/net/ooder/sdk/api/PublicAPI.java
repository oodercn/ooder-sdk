package net.ooder.sdk.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识公共 API 接口和类
 * <p>
 * 带有此注解的类或接口是 SDK 的公共 API，
 * 用户可以安全地依赖这些 API，SDK 团队将保证其向后兼容性。
 * </p>
 *
 * @since 3.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface PublicAPI {
    
    /**
     * API 稳定性级别
     */
    Stability stability() default Stability.STABLE;
    
    /**
     * API 说明
     */
    String description() default "";
    
    /**
     * API 稳定性级别枚举
     */
    enum Stability {
        /**
         * 稳定 API - 保证向后兼容
         */
        STABLE,
        
        /**
         * 实验性 API - 可能会变更
         */
        EXPERIMENTAL,
        
        /**
         * 已废弃 API - 将在未来版本移除
         */
        DEPRECATED
    }
}
