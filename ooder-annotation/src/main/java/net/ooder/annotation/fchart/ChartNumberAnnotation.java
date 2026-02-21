package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartNumberAnnotation {

    boolean formatnumber() default true;

    boolean formatnumberscale() default true;

    String defaultnumberscale() default "";

    String numberscaleunit() default "";

    String numberscalevalue() default "";

    boolean scalerecursively() default true;

    int maxscalerecursion() default -1;

    String scaleseparator() default "";

    String numberprefix() default "";

    String numbersuffix() default "";

    String decimalseparator() default "";

    String thousandseparator() default "";

    int thousandseparatorposition() default -1;

    String indecimalseparator() default "";

    String inthousandseparator() default "";

    int decimals() default -1;

    boolean forcedecimals() default true;

    boolean forceyaxisvaluedecimals() default true;

    int yaxisvaluedecimals() default -1;


}
