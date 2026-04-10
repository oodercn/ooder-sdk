package net.ooder.annotation;


import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD})
public @interface BtnAnnotation {

    @NotNull
    boolean infoBtn() default true;

    @NotNull
    boolean optBtn() default false;

    @NotNull
    boolean toggleBtn() default false;

    @NotNull
    boolean refreshBtn() default true;

    @NotNull
    boolean closeBtn() default true;

    boolean popBtn() default false;

}
