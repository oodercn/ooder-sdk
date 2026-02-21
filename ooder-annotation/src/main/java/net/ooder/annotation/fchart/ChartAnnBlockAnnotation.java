package net.ooder.annotation.fchart;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.AlignType;
import net.ooder.annotation.ui.VAlignType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartAnnBlockAnnotation {
    int index() default -1;

    boolean lazyLoad() default false;

    String id() default "";

    String caption() default "";

    FChartAnnType type() default FChartAnnType.annotation;

    int x() default -1;

    int y() default -1;

    int tox() default -1;

    int toy() default -1;

    String fillcolor() default "";

    int fillalpha() default -1;

    int fillratio() default -1;

    int fillngle() default -1;

    String fillpattern() default "";

    boolean showborder() default true;

    String bordercolor() default "";

    int borderalpha() default -1;

    int borderthickness() default -1;

    int dashed() default -1;

    int dashlen() default -1;

    int dashgap() default -1;

    String tooltext() default "";

    String link() default "";

    boolean showshadow() default true;

    String label() default "";

    AlignType align() default AlignType.center;

    VAlignType valign() default VAlignType.middle;

    String font() default "";

    int fontsize() default -1;

    String fontcolor() default "";

    boolean bold() default true;

    int italic() default -1;

    int leftmargin() default -1;

    String bgcolor() default "";

    String rotatetext() default "";

    int wrapwidth() default -1;

    int wrapheight() default -1;

    boolean wrap() default true;

    int radius() default -1;

    int yradius() default -1;

    int startangle() default -1;

    int endangle() default -1;

    int thickness() default -1;

    boolean showbelow() default true;

    boolean autoscale() default true;

    boolean constrainscale() default true;

    boolean scaletext() default true;

    boolean scaleimages() default true;

    int xshift() default -1;

    int yshift() default -1;

    int grpxshift() default -1;

    int grpyshift() default -1;

    String color() default "";

    int alpha() default -1;

    boolean visible() default true;

    AlignType textalign() default AlignType.center;

    VAlignType textvalign() default VAlignType.middle;

    boolean wraptext() default true;

    String path() default "";

    int origw() default -1;

    int righ() default -1;


    @NotNull
    boolean autoReload() default true;

    Class customItems() default Void.class;

    @NotNull
    Class bindService() default Void.class;

}
