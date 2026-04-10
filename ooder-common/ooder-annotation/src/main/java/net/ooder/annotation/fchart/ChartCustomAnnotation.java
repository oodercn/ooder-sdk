package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartCustomAnnotation {

    boolean animation() default true;

    int palette() default -1;

    String palettecolors() default "";

    boolean showlabels() default true;

    int maxlabelheight() default -1;

    boolean labeldisplay() default true;

    boolean useellipseswhenoverflow() default true;

    boolean rotatelabels() default true;

    boolean slantlabels() default true;

    int labelStep() default -1;

    int staggerlines() default -1;

    boolean showvalues() default true;

    boolean rotatevalues() default true;

    boolean placevaluesinside() default true;

    boolean showyaxisvalues() default true;

    boolean showlimits() default true;

    boolean showdivlinevalues() default true;

    int yaxisvaluesstep() default -1;

    boolean showshadow() default true;

    boolean adjustdiv() default true;

    boolean rotateyaxisname() default true;

    int yaxisnamewidth() default -1;

    String clickurl() default "";

    int yaxisminvalue() default -1;

    int yaxismaxvalue() default -1;

    int setadaptiveymin() default -1;

    boolean usedataplotcolorforlabels() default true;


}
