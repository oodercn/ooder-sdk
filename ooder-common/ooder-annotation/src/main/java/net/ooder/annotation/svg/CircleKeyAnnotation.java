package net.ooder.annotation.svg;


import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface CircleKeyAnnotation {
    String cx() default "0";

    String cy() default "0";

    @NotNull
    String r() default "25";

    String x() default "0";

    String y() default "0";

    @NotNull
    String fill() default "90-#5198D3-#A1C8F6";
    @NotNull
    String stroke() default "#004A7F";

    String title() default "";

}
