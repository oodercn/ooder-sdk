package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartPlotAnnotation {

    boolean showplotborder() default true;

    boolean useroundedges() default true;

    String plotbordercolor() default "";

    int plotborderthickness() default -1;

    int plotborderalpha() default -1;

    boolean plotborderdashed() default true;

    int plotborderdashlen() default -1;

    int plotborderdashgap() default -1;

    int plotfillangle() default -1;

    int plotfillratio() default -1;

    int plotfillalpha() default -1;

    String plotgradientcolor() default "";

}
