package net.ooder.annotation.svg;

import net.ooder.annotation.ui.CursorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface SVGBGText {
    String text() default "";

    String fontSize() default "";

    String fill() default "";

    String fontWight() default "";

    int strokeWidth() default 2;

    String stroke() default "";

    int fontStyle()default  2;

    CursorType cursor()default CursorType.crosshair;

}
