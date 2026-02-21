package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartDivisionalAnnotation {

    int numdivlines() default -1;

    String divlinecolor() default "";

    int divlinethickness() default -1;

    int divlinealpha() default -1;

    int divlinedashed() default -1;

    int divLinedashlen() default -1;

    int divLinedashgap() default -1;

    String zeroplanecolor() default "";

    int zeroplanethickness() default -1;

    int zeroplanealpha() default -1;

    boolean showzeroplanevalue() default true;

    boolean showalternatevgridcolor() default true;

    String alternatevgridcolor() default "";

    int alternatevgridalpha() default -1;
}
