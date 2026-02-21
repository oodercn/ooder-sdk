package net.ooder.annotation;

import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ComponentAnnotation {

    String id() default "";

    String caption() default "";

    String[] enums() default {};

    Class<? extends Enum> enumClass() default Enum.class;

    @NotNull
    boolean uid() default false;

    @NotNull
    boolean pid() default false;

    boolean captionField() default false;

    String expression() default "";

    boolean readonly() default false;

    boolean disabled() default false;


    @NotNull
    boolean hidden() default false;

    int index() default 0;

    String tips() default "";

    String imageClass() default "";


}
