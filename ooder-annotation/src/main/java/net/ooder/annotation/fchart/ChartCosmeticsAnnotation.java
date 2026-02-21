package net.ooder.annotation.fchart;


import net.ooder.annotation.ui.HAlignType;
import net.ooder.annotation.ui.VAlignType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartCosmeticsAnnotation {


    String bgcolor() default "";

    int bgalpha() default 0;

    int bgratio() default 0;

    int bgangle() default 0;

    String bgimage() default "";

    int bgimagealpha() default 0;

    FChartDisplayMode bgimagedisplaymode() default FChartDisplayMode.fill;

    VAlignType bgimagevalign() default VAlignType.middle;

    HAlignType bgimagehalign() default HAlignType.center;

    int bgimagescale() default -1;

    String canvasbgcolor() default "";

    int canvasbgalpha() default 0;

    int canvasbgratio() default 0;

    int canvasbgangle() default 0;

    String xaxislinecolor() default "";

    String canvasbordercolor() default "";

    int canvasborderthickness() default -1;

    int canvasborderalpha() default -1;

    boolean showborder() default true;

    String bordercolor() default "";

    int borderthickness() default -1;

    int borderalpha() default -1;

    boolean showvlinelabelborder() default true;

    String logourl() default "";

    FChartLogoPosition logoposition() default FChartLogoPosition.CC;

    ;

    int logoalpha() default 0;

    int logoscale() default -1;

    String logolink() default "";


}
