package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartPaddingsAnnotation {


    int captionpadding() default -1;

    int xaxisnamepadding() default -1;

    int yaxisnamepadding() default -1;

    int yaxisvaluespadding() default -1;

    int labelpadding() default -1;

    int valuepadding() default -1;

    int plotspacepercent() default -1;

    int chartleftmargin() default -1;

    int chartrightmargin() default -1;

    int charttopmargin() default -1;

    int chartbottommargin() default -1;

    int canvasleftmargin() default -1;

    int canvasrightmargin() default -1;

    int canvastopmargin() default -1;

    int canvasbottommargin() default -1;


}
