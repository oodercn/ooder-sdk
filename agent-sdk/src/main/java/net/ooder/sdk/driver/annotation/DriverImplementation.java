package net.ooder.sdk.driver.annotation;

import java.lang.annotation.*;

/**
 * Driver 实现注解
 * 用于标记 Driver 实现类
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DriverImplementation {

    /**
     * Driver 类型
     *
     * @return Driver 类型
     */
    String type();

    /**
     * Driver 名称
     *
     * @return Driver 名称
     */
    String name() default "";

    /**
     * Driver 版本
     *
     * @return Driver 版本
     */
    String version() default "1.0.0";

    /**
     * Driver 描述
     *
     * @return Driver 描述
     */
    String description() default "";

    /**
     * 是否默认实现
     *
     * @return 是否默认
     */
    boolean isDefault() default false;
}
