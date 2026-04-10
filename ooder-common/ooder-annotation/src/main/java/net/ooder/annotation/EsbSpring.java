package net.ooder.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Inherited
@Retention(value = RUNTIME)
@Target({ElementType.FIELD})
public @interface EsbSpring {

    public String id() default "";

    public String expressionArr() default "";

    public String name() default "";

    public int version() default 0;
}
