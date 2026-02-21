package net.ooder.annotation.svg;


import net.ooder.annotation.ui.CursorType;
import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ConnectorKeyAnnotation {
    String fill() default "";

    String stroke() default "";

    String title() default "";

    @NotNull
    int strokeWidth() default 3;

    String path() default "M,140,250L,200,250";

    @NotNull
    String arrowStart() default "oval-midium-midium";

    @NotNull
    String arrowEnd() default "classic-wide-long";

    CursorType cursor() default CursorType.crosshair;

}
