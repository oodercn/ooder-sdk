package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartAnnotation {
    String caption() default "";

    String subcaption() default "";

    String xaxisname() default "";

    String yaxisname() default "";
}
