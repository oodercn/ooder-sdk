package net.ooder.annotation.svg;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.CursorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface SVGText {
    String text() default "";

    @NotNull
    String fontSize() default "12px";

    @NotNull
    String fill() default "#000000";

    String fontWight() default "";

    int strokeWidth() default 2;

    String stroke() default "";

    String fontStyle() default "";

    CursorType cursor() default CursorType.crosshair;


}
