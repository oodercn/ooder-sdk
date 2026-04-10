package net.ooder.annotation.svg;

import com.alibaba.fastjson.annotation.JSONField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface SVGAttrAnnotation {


    String rx() default "0";

    String ry() default "0";

    String x() default "0";

    String y() default "0";

    String r() default "0";

    String cx() default "0";

    String cy() default "0";

    String stroke() default "";

    String fill() default "";

    String path() default "";


    String text() default "";


    String src() default "";


    String[] transform() default {};


    @JSONField(name = "stroke-width")
    int strokewidth() default 0;

    @JSONField(name = "stroke-dasharray")
    int strokedasharray() default 0;

    @JSONField(name = "stroke-linecap")
    String strokelinecap() default "";

    @JSONField(name = "stroke-opacity")
    long strokeopacity() default 0;


    @JSONField(name = "stroke-linejoin")
    String strokelinejoin() default "";

    @JSONField(name = "stroke-miterlimit")
    int strokemiterlimit() default 0;


    @JSONField(name = "arrow-end")
    String arrowend() default "";

    @JSONField(name = "arrow-start")
    String arrowstart() default "";

    String title() default "";

    PathKeyAnnotation KEY() default @PathKeyAnnotation(path = "");

    SVGTextAnnotation TEXT() default @SVGTextAnnotation;

    SVGBGText BG() default @SVGBGText;
}
