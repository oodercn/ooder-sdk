package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartToolTipAnnotation {


    String showtooltip() default "";

    String tooltipcolor() default "";

    String tooltipbgcolor() default "";

    String tooltipbordercolor() default "";

    String tooltipsepchar() default "";

    String seriesnameintooltip() default "";

    String showtooltipshadow() default "";


}
