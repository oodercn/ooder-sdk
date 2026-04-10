package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartLabelAnnotation {

    String labelfont() default "";

    String labelfontcolor() default "";

    int labelfontsize() default -1;

    boolean labefontbold() default true;

    boolean labelfontitalic() default true;

    String labelbgcolor() default "";

    String labelbordercolor() default "";

    int labelalpha() default -1;

    int labelbgalpha() default -1;

    int labelborderalpha() default -1;

    int labelborderpadding() default -1;

    int labelborderradius() default -1;

    int labelborderthickness() default -1;

    boolean labelborderdashed() default true;

    boolean labelborderdashLen() default true;

    int labelborderdashgap() default -1;

    String labellink() default "";


}
