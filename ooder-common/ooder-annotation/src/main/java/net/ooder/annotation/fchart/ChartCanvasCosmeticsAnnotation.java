package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartCanvasCosmeticsAnnotation {
    String canvasbgcolor() default "";

    int canvasbgalpha() default -1;

    int canvasbgratio() default -1;

    int canvasbgangle() default -1;

    String canvasbordercolor() default "";

    int canvasborderthickness() default -1;

    int canvasborderalpha() default -1;
}
