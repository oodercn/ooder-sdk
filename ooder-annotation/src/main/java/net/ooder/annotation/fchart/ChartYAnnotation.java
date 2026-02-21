package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartYAnnotation {

    String yaxisname() default "";

    int yaxisvaluesstep() default -1;

    int yaxismaxvalue() default -1;

    String yaxisnamefont() default "";

    String yaxisnamefontcolor() default "";

    int yaxisnamefontsize() default -1;

    boolean yaxisnamefontbold() default false;

    boolean yaxisnamefontitalic() default false;

    String yaxisnamebgcolor() default "";

    String yaxisnamebordercolor() default "";

}
