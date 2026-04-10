package net.ooder.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface AnimStatusAnnotation {
    String left() default "";

    String top() default "";

    String width() default "";

    String height() default "";

    String rotate() default "";

    double translateX() default 0;

    double translateY() default 0;

    double scaleX() default 0;

    double scaleY() default 0;

    String skewX() default "";

    String skewY() default "";

    String color() default "";

    String fontSize() default "";

    int lineHeight() default 0;

    String backgroundColor() default "";

    String backgroundPositionX() default "";

    String backgroundPositionY() default "";


}
