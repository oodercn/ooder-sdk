package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartScrollBarAnnotation {
    String scrollcolor() default "";

    int scrollheight() default -1;

    int scrollpadding() default -1;

    int crollbtnwidth() default -1;

    int scrollbtnpadding() default -1;
}
