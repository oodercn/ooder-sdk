package net.ooder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ooder
 * @version $Id: PropertyChinaName.java
 * @since 2025-08-25
 */
@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.METHOD)
public @interface MethodExpressionChinaName {
    String cname() default "无";
}
