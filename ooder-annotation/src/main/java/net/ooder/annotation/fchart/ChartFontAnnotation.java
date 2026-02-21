package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartFontAnnotation {

    String basefont();

    int basefontsize() default -1;

    String basefontcolor() default "";

    String outcnvbasefont() default "";

    int outcnvbasefontsize() default -1;

    String outcnvbasefontcolor() default "";


}
