package net.ooder.annotation.view;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.ResponsePathTypeEnum;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ButtonViewsViewAnnotation {

    String expression() default "";

    String saveUrl() default "";

    String dataUrl() default "";

    String reSetUrl() default "";
    @NotNull
    ResponsePathTypeEnum itemType() default ResponsePathTypeEnum.TABS;


}
