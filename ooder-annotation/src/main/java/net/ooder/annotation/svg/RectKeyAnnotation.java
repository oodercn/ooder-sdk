package net.ooder.annotation.svg;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface RectKeyAnnotation {

    int strokeMiterlimit() default 0;

    int strokeOpacity() default 1;

    int strokeWidth() default 0;

    String[] transform() default {};

    int width() default 80;

    int height() default 40;

    String x() default "0";;

    String y() default "0";;

    String fill() default "";

    String stroke() default "";

    String title() default "";

}
