package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartXAnnotation {


    String xaxisnamefontcolor() default "";

    int xaxisnamefontsize() default -1;

    boolean xaxisnamefontbold() default true;

    boolean Axisnamefontitalic() default true;

    String xaxisnamebgcolor() default "";

    String xaxisnamebordercolor() default "";

    int xaxisnamealpha() default -1;

    int xaxisnamefontalpha() default -1;

    int xaxisnamebgalpha() default -1;

    int xaxisnameborderalpha() default -1;

    int xaxisnameborderpadding() default -1;

    int xaxisnameborderradius() default -1;

    int xaxisnameborderthickness() default -1;

    boolean xaxisnameborderdashed() default true;

    int xaxisnameborderdashlen() default -1;

    int xaxisnameborderdashgap() default -1;

}
