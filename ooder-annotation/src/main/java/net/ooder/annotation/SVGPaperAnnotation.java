package net.ooder.annotation;

import net.ooder.annotation.ui.OverflowType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface SVGPaperAnnotation {
    boolean selectable() default true;

    String iframeAutoLoad() default "";

    String html() default "";

    String width() default "32.0em";

    String height() default "25.0em";

    OverflowType overflow() default OverflowType.auto;

    boolean scaleChildren() default false;

    int graphicZIndex() default 0;


}
