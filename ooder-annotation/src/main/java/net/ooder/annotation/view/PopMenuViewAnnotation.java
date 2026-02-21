package net.ooder.annotation.view;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.ResponsePathTypeEnum;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PopMenuViewAnnotation {


    String expression() default "";

    String saveUrl() default "";

    String dataPath() default "";

    String fieldCaption() default "";

    String fieldId() default "";
    @NotNull
    boolean cache() default true;

    boolean dynLoad() default false;
    @NotNull
    ResponsePathTypeEnum itemType() default ResponsePathTypeEnum.POPMENU;


}
