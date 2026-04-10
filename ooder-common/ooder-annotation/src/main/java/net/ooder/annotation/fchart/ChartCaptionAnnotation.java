package net.ooder.annotation.fchart;

import net.ooder.annotation.ui.AlignType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartCaptionAnnotation {

    AlignType captionalignment() default AlignType.center;

    boolean captionontop() default true;

    int captionfontsize() default -1;

    int subcaptionfontsize() default -1;

    String captionfont() default "";

    String subcaptionfont() default "";

    String captionfontcolor() default "";

    String subcaptionfontcolor() default "";

    boolean captionfontbold() default false;

    boolean subcaptionfontbold() default false;

    boolean aligncaptionwithcanvas() default true;

    int captionhorizontalpadding() default 0;


}
