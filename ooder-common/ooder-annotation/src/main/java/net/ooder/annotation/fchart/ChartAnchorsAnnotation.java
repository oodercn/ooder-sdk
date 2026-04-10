package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartAnchorsAnnotation {

    boolean drawanchors() default true;

    int anchorsides() default -1;

    int anchorradius() default -1;

    String anchorbordercolor() default "";

    int anchorborderthickness() default -1;

    String anchorbgcolor() default "";

    int anchoralpha() default -1;

    int anchorbgalpha() default -1;
}
