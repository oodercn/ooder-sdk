package net.ooder.annotation.svg;


import net.ooder.annotation.ui.UIPositionType;
import net.ooder.annotation.ui.VisibilityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface SVGAnnotation {

    String svgTag() default "";

    boolean selectable() default true;

    boolean defaultFocus() default true;

    VisibilityType visibility() default VisibilityType.visible;

    String renderer() default "";

    UIPositionType position() default UIPositionType.RELATIVE;

    String path() default "";

    // tabindex/zIndex is for compitable only
    boolean tabindex() default true;

    boolean zIndex() default true;

    boolean disableClickEffect() default false;

    boolean disableHoverEffect() default false;

    boolean disableTips() default false;

    boolean disabled() default false;

    String left() default "";

    String top() default "";

    String right() default "";

    String bottom() default "";

    String width() default "";

    String height() default "";

    boolean shadow() default true;

    String animDraw() default "";

    String offSetFlow() default "";

    String hAlign() default "";

    String vAlign() default "";

    String text() default "";
}
