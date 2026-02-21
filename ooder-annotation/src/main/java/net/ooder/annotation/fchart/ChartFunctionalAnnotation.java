package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})

public @interface ChartFunctionalAnnotation {
    boolean animation() default true;

    boolean animate3D() default true;

    int exetime() default -1;

    int palette() default -1;

    String palettecolors() default "";

    boolean connectnulldata() default true;

    boolean showlabels() default true;

    int maxlabelheight() default -1;

    boolean showvalues() default true;

    int labelstep() default -1;

    int yaxisvaluesstep() default -1;

    boolean showyaxisvalues() default true;

    boolean showlimits() default true;

    boolean showdivlinevalues() default true;

    boolean adjustdiv() default true;

    String clickurl() default "";

    int yaxisminvalue() default -1;

    int yaxismaxvalue() default -1;

    int setadaptiveymin() default -1;

    String xaxistickcolor() default "";

    int xaxistickalpha() default -1;

    int xaxistickthickness() default -1;
}
