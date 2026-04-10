package net.ooder.annotation.view;


import net.ooder.annotation.ui.ResponsePathTypeEnum;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD})
public @interface PopTreeViewAnnotation {
    @NotNull
    boolean autoSave() default false;

    String expression() default "";

    String saveUrl() default "";

    String caption() default "";

    String dataUrl() default "";

    @NotNull
    String rootId() default "00000000-0000-0000-0000-000000000000";

    String editorPath() default "";

    String loadChildUrl() default "";

    String fieldCaption() default "";

    Class bindClass() default Void.class;

    String fieldId() default "";

    @NotNull
    ResponsePathTypeEnum itemType() default ResponsePathTypeEnum.TREEVIEW;


}
