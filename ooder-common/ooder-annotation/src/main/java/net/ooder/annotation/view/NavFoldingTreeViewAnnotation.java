package net.ooder.annotation.view;

import net.ooder.annotation.ui.ResponsePathTypeEnum;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NavFoldingTreeViewAnnotation {


    String saveUrl() default "";

    String expression() default "";

    String dataUrl() default "";
    @NotNull
    boolean autoSave() default false;
    @NotNull
    ResponsePathTypeEnum itemType() default ResponsePathTypeEnum.TREEVIEW;


}
