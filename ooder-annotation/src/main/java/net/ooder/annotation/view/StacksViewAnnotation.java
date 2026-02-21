package net.ooder.annotation.view;


import net.ooder.annotation.ui.ResponsePathTypeEnum;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface StacksViewAnnotation {

    String expression() default "";

    String saveUrl() default "";

    String editorPath() default "";

    @NotNull
    ResponsePathTypeEnum itemType() default ResponsePathTypeEnum.TABS;


}
