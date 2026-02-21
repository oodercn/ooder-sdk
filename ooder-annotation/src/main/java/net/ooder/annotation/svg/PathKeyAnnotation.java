package net.ooder.annotation.svg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface PathKeyAnnotation {
    String path();

    String x() default "-1";;

    String y() default "-1";;

    String fill() default "";

    String stroke() default "";

    String title() default "";
}
