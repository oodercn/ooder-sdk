package net.ooder.annotation.view;

import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NavButtonLayoutViewAnnotation {

    String expression() default "";

    String dataUrl() default "";

    @NotNull
    boolean cache() default false;


}
