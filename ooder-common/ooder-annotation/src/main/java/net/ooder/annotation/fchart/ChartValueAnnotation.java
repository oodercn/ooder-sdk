package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartValueAnnotation {


    String valuefont() default "";

    String valuefontcolor() default "";

    int valuefontsize() default -1;

    boolean valuefontbold() default true;

    boolean valuefontitalic() default true;

    String valuebgcolor() default "";

    String valuebordercolor() default "";

    int valuealpha() default -1;

    int valuefontalpha() default -1;

    int valuebgalpha() default -1;

    int valueborderalpha() default -1;

    int valueborderthickness() default -1;

    int valueborderradius() default -1;

    boolean valueborderdashed() default true;

    int valueborderdashgap() default -1;

    int valueborderdashlen() default -1;

    int valuehoveralpha() default -1;

    int valuefonthoveralpha() default -1;

    int valuebghoveralpha() default -1;

    int valueborderhoveralpha() default -1;

    boolean showvaluesonhover() default true;

}
