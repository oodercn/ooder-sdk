package net.ooder.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Disabled {

    boolean disabled() default false;

    boolean disableClickEffect() default false;

    boolean disableHoverEffect() default false;

    boolean disableTips() default false;

    boolean defaultFocus() default false;

}
