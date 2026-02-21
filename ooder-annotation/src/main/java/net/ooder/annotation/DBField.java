package net.ooder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ooder
 * @since 2025-08-25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface DBField {
    String dbFieldName();

    int fractions() default 0;

    ColType dbType() default ColType.VARCHAR;

    String cnName() default "";

    String[] enums() default {};

    Class<? extends Enum> enumClass() default Enum.class;

    boolean isNull() default true;

    int length() default 64;
}
