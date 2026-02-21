package net.ooder.annotation.svg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface SVGKeyAnnotation {

    String x() default "0";

    String y() default "0";

    String fill() default "";

    String stroke() default "";

    String title() default "";
}
