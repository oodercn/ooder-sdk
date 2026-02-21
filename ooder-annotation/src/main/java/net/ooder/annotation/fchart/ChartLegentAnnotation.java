package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartLegentAnnotation {
    boolean showlegend() default true;

    boolean legenditemfontbold() default true;

    String legenditemfont() default "";

    int legenditemfontSize() default -1;

    String legenditemfontcolor() default "";

    String legenditemHoverfontcolor() default "";

    boolean legendcaptionalignment() default true;

    boolean legendcaptionbold() default false;

    String legendcaptionfont() default "";

    int legendcaptionfontsize() default -1;

    boolean legendiconscale() default true;

    String legenditemhiddencolor() default "";

    LegendPosition legendposition() default LegendPosition.NONE;

    String legendbgcolor() default "";

    int legendbgalpha() default -1;

    String legendbordercolor() default "";

    int legendborderthickness() default -1;

    int legendborderalpha() default -1;

    boolean legendshadow() default true;

    boolean legendallowdrag() default true;

    String legendscrollbgcolor() default "";

    boolean reverselegend() default true;

    boolean interactivelegend() default true;

    int legendnumcolumns() default -1;

    boolean minimise2rappinginlegend() default true;

}
