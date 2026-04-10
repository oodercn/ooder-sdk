package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartQuadrantsAnnotation {


    boolean drawquadrant() default true;

    int quadrantxval() default -1;

    int quadrantyval() default -1;

    String quadrantlinecolor() default "";

    int quadrantlinethickness() default -1;

    int quadrantlinealpha() default -1;

    int quadrantlinedashed() default -1;

    int quadrantlinedashlen() default -1;

    int quadrantlinedashgap() default -1;

    String quadrantlabeltl() default "";

    String quadrantlabeltr() default "";

    String quadrantlabelbl() default "";

    String quadrantlabelbr() default "";

    int quadrantlabelpadding() default -1;
}
